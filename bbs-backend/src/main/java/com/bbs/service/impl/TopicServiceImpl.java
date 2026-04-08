package com.bbs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bbs.common.BusinessException;
import com.bbs.common.RedisUtils;
import com.bbs.dto.TopicCreateRequestDTO;
import com.bbs.dto.TopicUpdateRequestDTO;
import com.bbs.dto.TopicVO;
import com.bbs.entity.Forum;
import com.bbs.entity.Topic;
import com.bbs.entity.User;
import com.bbs.mapper.ForumMapper;
import com.bbs.mapper.TopicMapper;
import com.bbs.mapper.UserMapper;
import com.bbs.service.TopicService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 帖子服务实现类
 */
@Service
public class TopicServiceImpl implements TopicService {
    
    @Resource
    private TopicMapper topicMapper;
    
    @Resource
    private UserMapper userMapper;
    
    @Resource
    private ForumMapper forumMapper;
    
    @Resource
    private RedisUtils redisUtils;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public TopicVO createTopic(TopicCreateRequestDTO request, Long userId) {
        // 检查版块是否存在
        Forum forum = forumMapper.selectById(request.getForumId());
        if (forum == null || forum.getStatus() != 1) {
            throw new BusinessException("版块不存在或已关闭");
        }
        
        // 检查用户状态
        User user = userMapper.selectById(userId);
        if (user == null || user.getStatus() != 1) {
            throw new BusinessException("用户不存在或已被禁用");
        }
        
        // 创建帖子
        Topic topic = new Topic();
        topic.setForumId(request.getForumId());
        topic.setUserId(userId);
        topic.setTitle(request.getTitle());
        topic.setContent(request.getContent());
        topic.setReplyCount(0);
        topic.setViewCount(0);
        topic.setLikeCount(0);
        topic.setIsTop(0);
        topic.setIsEssence(0);
        topic.setIsLock(0);
        topic.setStatus(1);
        topic.setCreatedAt(LocalDateTime.now());
        topic.setUpdatedAt(LocalDateTime.now());
        
        topicMapper.insert(topic);
        
        // 更新用户发帖数
        user.setPostCount(user.getPostCount() + 1);
        userMapper.updateById(user);
        
        // 更新版块主题帖数
        forum.setTopicCount(forum.getTopicCount() + 1);
        forumMapper.updateById(forum);
        
        return getTopicVO(topic);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public TopicVO updateTopic(Long topicId, TopicUpdateRequestDTO request, Long userId) {
        Topic topic = topicMapper.selectById(topicId);
        if (topic == null || topic.getStatus() != 1) {
            throw new BusinessException("帖子不存在");
        }
        
        // 检查权限（作者本人或管理员）
        if (!topic.getUserId().equals(userId)) {
            User user = userMapper.selectById(userId);
            if (user == null || user.getRole() < 2) {
                throw new BusinessException("无权限修改此帖子");
            }
        }
        
        topic.setTitle(request.getTitle());
        topic.setContent(request.getContent());
        topic.setUpdatedAt(LocalDateTime.now());
        
        topicMapper.updateById(topic);
        
        // 清除缓存
        redisUtils.delete("topic:" + topicId);
        
        return getTopicVO(topic);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTopic(Long topicId, Long userId) {
        Topic topic = topicMapper.selectById(topicId);
        if (topic == null || topic.getStatus() != 1) {
            throw new BusinessException("帖子不存在");
        }
        
        // 检查权限
        if (!topic.getUserId().equals(userId)) {
            User user = userMapper.selectById(userId);
            if (user == null || user.getRole() < 2) {
                throw new BusinessException("无权限删除此帖子");
            }
        }
        
        // 逻辑删除
        topic.setStatus(0);
        topic.setUpdatedAt(LocalDateTime.now());
        topicMapper.updateById(topic);
        
        // 更新用户发帖数
        User user = userMapper.selectById(topic.getUserId());
        if (user != null) {
            user.setPostCount(Math.max(0, user.getPostCount() - 1));
            userMapper.updateById(user);
        }
        
        // 更新版块主题帖数
        Forum forum = forumMapper.selectById(topic.getForumId());
        if (forum != null) {
            forum.setTopicCount(Math.max(0, forum.getTopicCount() - 1));
            forumMapper.updateById(forum);
        }
        
        // 清除缓存
        redisUtils.delete("topic:" + topicId);
    }
    
    @Override
    public TopicVO getTopicById(Long topicId) {
        // 先从缓存获取
        String cacheKey = "topic:" + topicId;
        Object cached = redisUtils.get(cacheKey);
        if (cached != null) {
            return (TopicVO) cached;
        }
        
        Topic topic = topicMapper.selectById(topicId);
        if (topic == null || topic.getStatus() != 1) {
            throw new BusinessException("帖子不存在");
        }
        
        TopicVO vo = getTopicVO(topic);
        
        // 存入缓存（1小时过期）
        redisUtils.setEx(cacheKey, vo, 3600);
        
        return vo;
    }
    
    @Override
    public List<TopicVO> getTopicsByForumId(Long forumId, Integer pageNum, Integer pageSize, String sort) {
        Page<Topic> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Topic> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Topic::getForumId, forumId)
                .eq(Topic::getStatus, 1);
        
        // 排序
        if ("hot".equals(sort)) {
            // 热门排序：置顶 > 精华 > 回复数 > 创建时间
            wrapper.orderByDesc(Topic::getIsTop)
                    .orderByDesc(Topic::getIsEssence)
                    .orderByDesc(Topic::getReplyCount)
                    .orderByDesc(Topic::getCreatedAt);
        } else {
            // 最新排序：置顶 > 创建时间
            wrapper.orderByDesc(Topic::getIsTop)
                    .orderByDesc(Topic::getCreatedAt);
        }
        
        Page<Topic> result = topicMapper.selectPage(page, wrapper);
        
        return result.getRecords().stream()
                .map(this::getTopicVO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<TopicVO> getHotTopics(Integer pageNum, Integer pageSize) {
        // 先从Redis获取缓存的热门帖子
        String hotKey = "hot:topics";
        Object cached = redisUtils.get(hotKey);
        if (cached != null) {
            return (List<TopicVO>) cached;
        }
        
        // 从数据库获取
        Page<Topic> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Topic> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Topic::getStatus, 1)
                .orderByDesc(Topic::getIsTop)
                .orderByDesc(Topic::getIsEssence)
                .orderByDesc(Topic::getReplyCount)
                .orderByDesc(Topic::getViewCount);
        
        Page<Topic> result = topicMapper.selectPage(page, wrapper);
        List<TopicVO> list = result.getRecords().stream()
                .map(this::getTopicVO)
                .collect(Collectors.toList());
        
        // 存入Redis缓存（10分钟过期）
        redisUtils.setEx(hotKey, list, 600);
        
        return list;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setTopicTop(Long topicId, Integer topType) {
        Topic topic = topicMapper.selectById(topicId);
        if (topic == null || topic.getStatus() != 1) {
            throw new BusinessException("帖子不存在");
        }
        
        topic.setIsTop(topType);
        topic.setUpdatedAt(LocalDateTime.now());
        topicMapper.updateById(topic);
        
        // 清除缓存
        redisUtils.delete("topic:" + topicId);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setTopicEssence(Long topicId, Integer isEssence) {
        Topic topic = topicMapper.selectById(topicId);
        if (topic == null || topic.getStatus() != 1) {
            throw new BusinessException("帖子不存在");
        }
        
        topic.setIsEssence(isEssence);
        topic.setUpdatedAt(LocalDateTime.now());
        topicMapper.updateById(topic);
        
        // 清除缓存
        redisUtils.delete("topic:" + topicId);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setTopicLock(Long topicId, Integer isLock) {
        Topic topic = topicMapper.selectById(topicId);
        if (topic == null || topic.getStatus() != 1) {
            throw new BusinessException("帖子不存在");
        }
        
        topic.setIsLock(isLock);
        topic.setUpdatedAt(LocalDateTime.now());
        topicMapper.updateById(topic);
        
        // 清除缓存
        redisUtils.delete("topic:" + topicId);
    }
    
    @Override
    public void incrementViewCount(Long topicId) {
        // 异步增加浏览数，这里简单处理直接更新
        Topic topic = topicMapper.selectById(topicId);
        if (topic != null) {
            topic.setViewCount(topic.getViewCount() + 1);
            topicMapper.updateById(topic);
        }
    }
    
    /**
     * 将Topic实体转换为TopicVO
     */
    private TopicVO getTopicVO(Topic topic) {
        TopicVO vo = new TopicVO();
        vo.setId(topic.getId());
        vo.setForumId(topic.getForumId());
        vo.setUserId(topic.getUserId());
        vo.setTitle(topic.getTitle());
        vo.setContent(topic.getContent());
        vo.setReplyCount(topic.getReplyCount());
        vo.setViewCount(topic.getViewCount());
        vo.setLikeCount(topic.getLikeCount());
        vo.setIsTop(topic.getIsTop());
        vo.setIsEssence(topic.getIsEssence());
        vo.setIsLock(topic.getIsLock());
        vo.setLastReplyUserId(topic.getLastReplyUserId());
        vo.setLastReplyTime(topic.getLastReplyTime());
        vo.setStatus(topic.getStatus());
        vo.setCreatedAt(topic.getCreatedAt());
        vo.setUpdatedAt(topic.getUpdatedAt());
        
        // 获取用户信息
        User user = userMapper.selectById(topic.getUserId());
        if (user != null) {
            vo.setUsername(user.getUsername());
            vo.setNickname(user.getNickname());
            vo.setAvatar(user.getAvatar());
        }
        
        // 获取版块名称
        Forum forum = forumMapper.selectById(topic.getForumId());
        if (forum != null) {
            vo.setForumName(forum.getName());
        }
        
        // 获取最后回复人用户名
        if (topic.getLastReplyUserId() != null) {
            User lastReplyUser = userMapper.selectById(topic.getLastReplyUserId());
            if (lastReplyUser != null) {
                vo.setLastReplyUsername(lastReplyUser.getUsername());
            }
        }
        
        return vo;
    }
}