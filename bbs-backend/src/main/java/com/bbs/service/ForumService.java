package com.bbs.service;

import com.bbs.dto.ForumCreateRequestDTO;
import com.bbs.dto.ForumVO;

import java.util.List;

/**
 * 版块服务接口
 */
public interface ForumService {
    
    /**
     * 获取版块树形列表
     */
    List<ForumVO> getForumTree();
    
    /**
     * 获取所有开放版块列表
     */
    List<ForumVO> getForumList();
    
    /**
     * 根据ID获取版块详情
     */
    ForumVO getForumById(Long forumId);
    
    /**
     * 创建版块
     */
    ForumVO createForum(ForumCreateRequestDTO request, Long userId);
    
    /**
     * 更新版块
     */
    ForumVO updateForum(Long forumId, ForumCreateRequestDTO request);
    
    /**
     * 删除版块
     */
    void deleteForum(Long forumId);
}