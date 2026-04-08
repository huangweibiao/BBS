package com.bbs.service;

import com.bbs.dto.ReplyCreateRequestDTO;
import com.bbs.dto.ReplyVO;

import java.util.List;

/**
 * 回复服务接口
 */
public interface ReplyService {
    
    /**
     * 创建回复
     */
    ReplyVO createReply(Long topicId, ReplyCreateRequestDTO request, Long userId);
    
    /**
     * 获取帖子回复列表（楼中楼结构）
     */
    List<ReplyVO> getRepliesByTopicId(Long topicId);
    
    /**
     * 删除回复
     */
    void deleteReply(Long replyId, Long userId);
}