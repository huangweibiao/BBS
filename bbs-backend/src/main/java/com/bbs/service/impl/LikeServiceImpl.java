package com.bbs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bbs.common.BusinessException;
import com.bbs.dto.LikeRequestDTO;
import com.bbs.entity.Like;
import com.bbs.entity.Topic;
import com.bbs.entity.Reply;
import com.bbs.mapper.LikeMapper;
import com.bbs.mapper.TopicMapper;
import com.bbs.mapper.ReplyMapper;
import com.bbs.service.LikeService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 点赞服务实现类
 */
@Service
public class LikeServiceImpl implements LikeService {
    
    @Resource
    private LikeMapper likeMapper;
    
    @Resource
    private TopicMapper topicMapper;
    
    @Resource
    private ReplyMapper replyMapper;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String toggleLike(LikeRequestDTO request, Long userId) {
        // 检查目标是否存在
        if (request.getTargetType() == 1) {
            // 帖子
            Topic topic = topicMapper.selectById(request.getTargetId());
            if (topic == null || topic.getStatus() != 1) {
                throw new BusinessException("帖子不存在");
            }
        } else if (request.getTargetType() == 2) {
            // 回复
            Reply reply = replyMapper.selectById(request.getTargetId());
            if (reply == null || reply.getStatus() != 1) {
                throw new BusinessException("回复不存在");
            }
        }
        
        // 检查是否已点赞
        LambdaQueryWrapper<Like> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Like::getUserId, userId)
                .eq(Like::getTargetType, request.getTargetType())
                .eq(Like::getTargetId, request.getTargetId());
        
        Like existingLike = likeMapper.selectOne(wrapper);
        
        if (existingLike != null) {
            // 取消点赞
            likeMapper.deleteById(existingLike.getId());
            
            // 更新目标点赞数
            updateLikeCount(request.getTargetType(), request.getTargetId(), -1);
            
            return "取消点赞成功";
        } else {
            // 点赞
            Like like = new Like();
            like.setUserId(userId);
            like.setTargetType(request.getTargetType());
            like.setTargetId(request.getTargetId());
            like.setCreatedAt(LocalDateTime.now());
            
            likeMapper.insert(like);
            
            // 更新目标点赞数
            updateLikeCount(request.getTargetType(), request.getTargetId(), 1);
            
            return "点赞成功";
        }
    }
    
    @Override
    public boolean hasLiked(Long userId, Integer targetType, Long targetId) {
        LambdaQueryWrapper<Like> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Like::getUserId, userId)
                .eq(Like::getTargetType, targetType)
                .eq(Like::getTargetId, targetId);
        
        return likeMapper.selectCount(wrapper) > 0;
    }
    
    /**
     * 更新点赞数
     */
    private void updateLikeCount(Integer targetType, Long targetId, int delta) {
        if (targetType == 1) {
            Topic topic = topicMapper.selectById(targetId);
            if (topic != null) {
                topic.setLikeCount(topic.getLikeCount() + delta);
                topicMapper.updateById(topic);
            }
        } else if (targetType == 2) {
            Reply reply = replyMapper.selectById(targetId);
            if (reply != null) {
                reply.setLikeCount(reply.getLikeCount() + delta);
                replyMapper.updateById(reply);
            }
        }
    }
}