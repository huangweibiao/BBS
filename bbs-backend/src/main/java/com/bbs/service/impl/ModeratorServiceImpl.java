package com.bbs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bbs.common.BusinessException;
import com.bbs.dto.ModeratorRequestDTO;
import com.bbs.entity.Moderator;
import com.bbs.entity.User;
import com.bbs.mapper.ModeratorMapper;
import com.bbs.mapper.UserMapper;
import com.bbs.service.ModeratorService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 版主服务实现类
 */
@Service
public class ModeratorServiceImpl implements ModeratorService {
    
    @Resource
    private ModeratorMapper moderatorMapper;
    
    @Resource
    private UserMapper userMapper;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void appointModerator(ModeratorRequestDTO request) {
        // 检查用户是否存在
        User user = userMapper.selectById(request.getUserId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 检查是否已经是版主
        LambdaQueryWrapper<Moderator> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Moderator::getUserId, request.getUserId())
                .eq(Moderator::getForumId, request.getForumId());
        
        Long count = moderatorMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException("该用户已经是版主");
        }
        
        // 创建版主关联
        Moderator moderator = new Moderator();
        moderator.setUserId(request.getUserId());
        moderator.setForumId(request.getForumId());
        moderator.setCreatedAt(LocalDateTime.now());
        
        moderatorMapper.insert(moderator);
        
        // 更新用户角色为版主
        if (user.getRole() < 1) {
            user.setRole(1);
            user.setUpdatedAt(LocalDateTime.now());
            userMapper.updateById(user);
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeModerator(ModeratorRequestDTO request) {
        // 检查版主关联是否存在
        LambdaQueryWrapper<Moderator> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Moderator::getUserId, request.getUserId())
                .eq(Moderator::getForumId, request.getForumId());
        
        Long count = moderatorMapper.selectCount(wrapper);
        if (count == 0) {
            throw new BusinessException("该用户不是版主");
        }
        
        // 删除版主关联
        moderatorMapper.delete(wrapper);
        
        // 检查用户是否还有其他版主关联，如果没有则降为普通用户
        Long remainingCount = moderatorMapper.selectCount(
                new LambdaQueryWrapper<Moderator>().eq(Moderator::getUserId, request.getUserId())
        );
        
        if (remainingCount == 0) {
            User user = userMapper.selectById(request.getUserId());
            if (user != null && user.getRole() == 1) {
                user.setRole(0);
                user.setUpdatedAt(LocalDateTime.now());
                userMapper.updateById(user);
            }
        }
    }
    
    @Override
    public boolean isModerator(Long userId, Long forumId) {
        LambdaQueryWrapper<Moderator> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Moderator::getUserId, userId)
                .eq(Moderator::getForumId, forumId);
        
        return moderatorMapper.selectCount(wrapper) > 0;
    }
}