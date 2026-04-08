package com.bbs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bbs.common.BusinessException;
import com.bbs.dto.ReplyCreateRequestDTO;
import com.bbs.dto.ReplyVO;
import com.bbs.entity.Reply;
import com.bbs.entity.Topic;
import com.bbs.entity.User;
import com.bbs.mapper.ReplyMapper;
import com.bbs.mapper.TopicMapper;
import com.bbs.mapper.UserMapper;
import com.bbs.service.ReplyService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 回复服务实现类
 */
@Service
public class ReplyServiceImpl implements ReplyService {
    
    @Resource
    private ReplyMapper replyMapper;
    
    @Resource
    private TopicMapper topicMapper;
    
    @Resource
    private UserMapper userMapper;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReplyVO createReply(Long topicId, ReplyCreateRequestDTO request, Long userId) {
        // 检查帖子是否存在且未锁定
        Topic topic = topicMapper.selectById(topicId);
        if (topic == null || topic.getStatus() != 1) {
            throw new BusinessException("帖子不存在");
        }
        if (topic.getIsLock() == 1) {
            throw new BusinessException("帖子已锁定，无法回复");
        }
        
        // 如果是楼中楼回复，检查父回复是否存在
        if (request.getParentReplyId() != null && request.getParentReplyId() > 0) {
            Reply parentReply = replyMapper.selectById(request.getParentReplyId());
            if (parentReply == null || parentReply.getStatus() != 1) {
                throw new BusinessException("父回复不存在");
            }
        }
        
        // 创建回复
        Reply reply = new Reply();
        reply.setTopicId(topicId);
        reply.setUserId(userId);
        reply.setContent(request.getContent());
        reply.setParentReplyId(request.getParentReplyId() != null ? request.getParentReplyId() : 0L);
        reply.setReplyToUserId(request.getReplyToUserId());
        reply.setLikeCount(0);
        reply.setStatus(1);
        reply.setCreatedAt(LocalDateTime.now());
        reply.setUpdatedAt(LocalDateTime.now());
        
        replyMapper.insert(reply);
        
        // 更新帖子回复数
        topic.setReplyCount(topic.getReplyCount() + 1);
        topic.setLastReplyUserId(userId);
        topic.setLastReplyTime(LocalDateTime.now());
        topicMapper.updateById(topic);
        
        // 更新用户回帖数
        User user = userMapper.selectById(userId);
        if (user != null) {
            user.setReplyCount(user.getReplyCount() + 1);
            userMapper.updateById(user);
        }
        
        return getReplyVO(reply);
    }
    
    @Override
    public List<ReplyVO> getRepliesByTopicId(Long topicId) {
        // 查询所有正常状态的回复
        LambdaQueryWrapper<Reply> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Reply::getTopicId, topicId)
                .eq(Reply::getStatus, 1)
                .orderByAsc(Reply::getCreatedAt);
        
        List<Reply> replies = replyMapper.selectList(wrapper);
        
        // 转换为VO
        List<ReplyVO> replyVOs = replies.stream()
                .map(this::getReplyVO)
                .collect(Collectors.toList());
        
        // 构建楼中楼结构（一级评论 + 子评论）
        return buildTree(replyVOs);
    }
    
    /**
     * 构建树形结构
     */
    private List<ReplyVO> buildTree(List<ReplyVO> list) {
        // 找出一级评论（parentReplyId为0或null）
        List<ReplyVO> roots = list.stream()
                .filter(r -> r.getParentReplyId() == null || r.getParentReplyId() == 0L)
                .collect(Collectors.toList());
        
        // 为每个一级评论设置子评论
        for (ReplyVO root : roots) {
            root.setChildren(getChildren(root.getId(), list));
        }
        
        return roots;
    }
    
    /**
     * 获取子评论
     */
    private List<ReplyVO> getChildren(Long parentId, List<ReplyVO> list) {
        List<ReplyVO> children = list.stream()
                .filter(r -> parentId.equals(r.getParentReplyId()))
                .collect(Collectors.toList());
        
        // 如果有子评论，继续递归获取子评论的子评论（限制两级）
        for (ReplyVO child : children) {
            child.setChildren(new ArrayList<>());  // 第二级不再有子评论
        }
        
        return children.isEmpty() ? null : children;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteReply(Long replyId, Long userId) {
        Reply reply = replyMapper.selectById(replyId);
        if (reply == null || reply.getStatus() != 1) {
            throw new BusinessException("回复不存在");
        }
        
        // 检查权限
        if (!reply.getUserId().equals(userId)) {
            User user = userMapper.selectById(userId);
            if (user == null || user.getRole() < 2) {
                throw new BusinessException("无权限删除此回复");
            }
        }
        
        // 逻辑删除
        reply.setStatus(0);
        reply.setUpdatedAt(LocalDateTime.now());
        replyMapper.updateById(reply);
        
        // 更新帖子回复数
        Topic topic = topicMapper.selectById(reply.getTopicId());
        if (topic != null) {
            topic.setReplyCount(Math.max(0, topic.getReplyCount() - 1));
            topicMapper.updateById(topic);
        }
        
        // 更新用户回帖数
        User user = userMapper.selectById(reply.getUserId());
        if (user != null) {
            user.setReplyCount(Math.max(0, user.getReplyCount() - 1));
            userMapper.updateById(user);
        }
    }
    
    /**
     * 将Reply实体转换为ReplyVO
     */
    private ReplyVO getReplyVO(Reply reply) {
        ReplyVO vo = new ReplyVO();
        vo.setId(reply.getId());
        vo.setTopicId(reply.getTopicId());
        vo.setUserId(reply.getUserId());
        vo.setContent(reply.getContent());
        vo.setParentReplyId(reply.getParentReplyId());
        vo.setReplyToUserId(reply.getReplyToUserId());
        vo.setLikeCount(reply.getLikeCount());
        vo.setStatus(reply.getStatus());
        vo.setCreatedAt(reply.getCreatedAt());
        
        // 获取回复者信息
        User user = userMapper.selectById(reply.getUserId());
        if (user != null) {
            vo.setUsername(user.getUsername());
            vo.setNickname(user.getNickname());
            vo.setAvatar(user.getAvatar());
        }
        
        // 获取父回复者用户名
        if (reply.getParentReplyId() != null && reply.getParentReplyId() > 0) {
            Reply parentReply = replyMapper.selectById(reply.getParentReplyId());
            if (parentReply != null) {
                User parentUser = userMapper.selectById(parentReply.getUserId());
                if (parentUser != null) {
                    vo.setParentUsername(parentUser.getUsername());
                }
            }
        }
        
        // 获取回复目标用户名
        if (reply.getReplyToUserId() != null) {
            User replyToUser = userMapper.selectById(reply.getReplyToUserId());
            if (replyToUser != null) {
                vo.setReplyToUsername(replyToUser.getUsername());
            }
        }
        
        return vo;
    }
}