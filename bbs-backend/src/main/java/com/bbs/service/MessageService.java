package com.bbs.service;

import com.bbs.dto.MessageVO;

import java.util.List;

/**
 * 消息服务接口
 */
public interface MessageService {
    
    /**
     * 获取当前用户消息列表
     * @param userId 用户ID
     * @param type 消息类型（可选）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 消息列表
     */
    List<MessageVO> getMessages(Long userId, Integer type, Integer pageNum, Integer pageSize);
    
    /**
     * 获取未读消息数量
     */
    Long getUnreadCount(Long userId);
    
    /**
     * 标记消息为已读
     */
    void markAsRead(Long messageId, Long userId);
    
    /**
     * 标记所有消息为已读
     */
    void markAllAsRead(Long userId);
    
    /**
     * 发送消息
     * @param fromUserId 发送者ID（NULL为系统消息）
     * @param toUserId 接收者ID
     * @param type 消息类型
     * @param title 标题
     * @param content 内容
     */
    void sendMessage(Long fromUserId, Long toUserId, Integer type, String title, String content);
}