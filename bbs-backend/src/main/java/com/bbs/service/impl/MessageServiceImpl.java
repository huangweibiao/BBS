package com.bbs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bbs.common.BusinessException;
import com.bbs.dto.MessageVO;
import com.bbs.entity.Message;
import com.bbs.entity.User;
import com.bbs.mapper.MessageMapper;
import com.bbs.mapper.UserMapper;
import com.bbs.service.MessageService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 消息服务实现类
 */
@Service
public class MessageServiceImpl implements MessageService {
    
    @Resource
    private MessageMapper messageMapper;
    
    @Resource
    private UserMapper userMapper;
    
    @Override
    public List<MessageVO> getMessages(Long userId, Integer type, Integer pageNum, Integer pageSize) {
        Page<Message> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getToUserId, userId);
        if (type != null) {
            wrapper.eq(Message::getType, type);
        }
        wrapper.orderByDesc(Message::getCreatedAt);
        
        Page<Message> result = messageMapper.selectPage(page, wrapper);
        
        return result.getRecords().stream()
                .map(this::getMessageVO)
                .collect(Collectors.toList());
    }
    
    @Override
    public Long getUnreadCount(Long userId) {
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getToUserId, userId)
                .eq(Message::getIsRead, 0);
        return messageMapper.selectCount(wrapper);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAsRead(Long messageId, Long userId) {
        Message message = messageMapper.selectById(messageId);
        if (message == null) {
            throw new BusinessException("消息不存在");
        }
        if (!message.getToUserId().equals(userId)) {
            throw new BusinessException("无权限操作");
        }
        
        message.setIsRead(1);
        messageMapper.updateById(message);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAllAsRead(Long userId) {
        Message message = new Message();
        message.setIsRead(1);
        
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getToUserId, userId)
                .eq(Message::getIsRead, 0);
        
        messageMapper.update(message, wrapper);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendMessage(Long fromUserId, Long toUserId, Integer type, String title, String content) {
        Message message = new Message();
        message.setFromUserId(fromUserId);
        message.setToUserId(toUserId);
        message.setType(type);
        message.setTitle(title);
        message.setContent(content);
        message.setIsRead(0);
        message.setCreatedAt(LocalDateTime.now());
        
        messageMapper.insert(message);
    }
    
    /**
     * 将Message实体转换为MessageVO
     */
    private MessageVO getMessageVO(Message message) {
        MessageVO vo = new MessageVO();
        vo.setId(message.getId());
        vo.setFromUserId(message.getFromUserId());
        vo.setToUserId(message.getToUserId());
        vo.setType(message.getType());
        vo.setTitle(message.getTitle());
        vo.setContent(message.getContent());
        vo.setIsRead(message.getIsRead());
        vo.setCreatedAt(message.getCreatedAt());
        
        // 设置类型名称
        String[] typeNames = {"", "系统通知", "@提醒", "回复提醒", "私信"};
        if (message.getType() >= 1 && message.getType() <= 4) {
            vo.setTypeName(typeNames[message.getType()]);
        }
        
        // 获取发送者信息
        if (message.getFromUserId() != null) {
            User fromUser = userMapper.selectById(message.getFromUserId());
            if (fromUser != null) {
                vo.setFromUsername(fromUser.getUsername());
                vo.setFromAvatar(fromUser.getAvatar());
            }
        }
        
        return vo;
    }
}