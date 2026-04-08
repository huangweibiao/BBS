package com.bbs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bbs.common.BusinessException;
import com.bbs.dto.ForumCreateRequestDTO;
import com.bbs.dto.ForumVO;
import com.bbs.entity.Forum;
import com.bbs.mapper.ForumMapper;
import com.bbs.service.ForumService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 版块服务实现类
 */
@Service
public class ForumServiceImpl implements ForumService {
    
    @Resource
    private ForumMapper forumMapper;
    
    @Override
    public List<ForumVO> getForumTree() {
        // 查询所有开放的版块
        LambdaQueryWrapper<Forum> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Forum::getStatus, 1)
                .orderByAsc(Forum::getSortOrder);
        List<Forum> forums = forumMapper.selectList(wrapper);
        
        // 转换为VO
        List<ForumVO> forumVOs = forums.stream()
                .map(ForumVO::fromEntity)
                .collect(Collectors.toList());
        
        // 构建树形结构
        return buildTree(forumVOs);
    }
    
    /**
     * 构建树形结构
     */
    private List<ForumVO> buildTree(List<ForumVO> list) {
        // 获取顶级版块
        List<ForumVO> roots = list.stream()
                .filter(f -> f.getParentId() == null || f.getParentId() == 0L)
                .collect(Collectors.toList());
        
        // 递归构建子版块
        for (ForumVO root : roots) {
            root.setChildren(getChildren(root.getId(), list));
        }
        
        return roots;
    }
    
    /**
     * 获取子版块
     */
    private List<ForumVO> getChildren(Long parentId, List<ForumVO> list) {
        List<ForumVO> children = list.stream()
                .filter(f -> parentId.equals(f.getParentId()))
                .collect(Collectors.toList());
        
        for (ForumVO child : children) {
            child.setChildren(getChildren(child.getId(), list));
        }
        
        return children.isEmpty() ? null : children;
    }
    
    @Override
    public List<ForumVO> getForumList() {
        LambdaQueryWrapper<Forum> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Forum::getStatus, 1)
                .orderByAsc(Forum::getSortOrder);
        List<Forum> forums = forumMapper.selectList(wrapper);
        return forums.stream()
                .map(ForumVO::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Override
    public ForumVO getForumById(Long forumId) {
        Forum forum = forumMapper.selectById(forumId);
        if (forum == null) {
            throw new BusinessException("版块不存在");
        }
        return ForumVO.fromEntity(forum);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ForumVO createForum(ForumCreateRequestDTO request, Long userId) {
        Forum forum = new Forum();
        forum.setName(request.getName());
        forum.setDescription(request.getDescription());
        forum.setIcon(request.getIcon());
        forum.setParentId(request.getParentId() != null ? request.getParentId() : 0L);
        forum.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
        forum.setTopicCount(0);
        forum.setPostCount(0);
        forum.setStatus(1);
        forum.setCreatedAt(LocalDateTime.now());
        forum.setUpdatedAt(LocalDateTime.now());
        
        forumMapper.insert(forum);
        return ForumVO.fromEntity(forum);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ForumVO updateForum(Long forumId, ForumCreateRequestDTO request) {
        Forum forum = forumMapper.selectById(forumId);
        if (forum == null) {
            throw new BusinessException("版块不存在");
        }
        
        forum.setName(request.getName());
        forum.setDescription(request.getDescription());
        forum.setIcon(request.getIcon());
        if (request.getParentId() != null) {
            forum.setParentId(request.getParentId());
        }
        if (request.getSortOrder() != null) {
            forum.setSortOrder(request.getSortOrder());
        }
        forum.setUpdatedAt(LocalDateTime.now());
        
        forumMapper.updateById(forum);
        return ForumVO.fromEntity(forum);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteForum(Long forumId) {
        Forum forum = forumMapper.selectById(forumId);
        if (forum == null) {
            throw new BusinessException("版块不存在");
        }
        
        // 检查是否有子版块
        Long childCount = forumMapper.selectCount(
                new LambdaQueryWrapper<Forum>().eq(Forum::getParentId, forumId)
        );
        if (childCount > 0) {
            throw new BusinessException("请先删除子版块");
        }
        
        forumMapper.deleteById(forumId);
    }
}