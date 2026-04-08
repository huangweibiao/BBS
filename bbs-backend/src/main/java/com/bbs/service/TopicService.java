package com.bbs.service;

import com.bbs.dto.TopicCreateRequestDTO;
import com.bbs.dto.TopicUpdateRequestDTO;
import com.bbs.dto.TopicVO;

import java.util.List;

/**
 * 帖子服务接口
 */
public interface TopicService {
    
    /**
     * 创建帖子
     */
    TopicVO createTopic(TopicCreateRequestDTO request, Long userId);
    
    /**
     * 更新帖子
     */
    TopicVO updateTopic(Long topicId, TopicUpdateRequestDTO request, Long userId);
    
    /**
     * 删除帖子
     */
    void deleteTopic(Long topicId, Long userId);
    
    /**
     * 获取帖子详情
     */
    TopicVO getTopicById(Long topicId);
    
    /**
     * 获取版块帖子列表（分页）
     */
    List<TopicVO> getTopicsByForumId(Long forumId, Integer pageNum, Integer pageSize, String sort);
    
    /**
     * 获取热门帖子列表
     */
    List<TopicVO> getHotTopics(Integer pageNum, Integer pageSize);
    
    /**
     * 设置帖子置顶
     */
    void setTopicTop(Long topicId, Integer topType);
    
    /**
     * 设置帖子精华
     */
    void setTopicEssence(Long topicId, Integer isEssence);
    
    /**
     * 设置帖子锁定
     */
    void setTopicLock(Long topicId, Integer isLock);
    
    /**
     * 增加浏览数
     */
    void incrementViewCount(Long topicId);
}