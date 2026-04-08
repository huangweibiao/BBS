package com.bbs.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bbs.common.RedisUtils;
import com.bbs.dto.TopicVO;
import com.bbs.entity.Forum;
import com.bbs.entity.Topic;
import com.bbs.entity.User;
import com.bbs.mapper.ForumMapper;
import com.bbs.mapper.TopicMapper;
import com.bbs.mapper.UserMapper;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 热帖排行定时任务
 * 每10分钟计算一次热门帖子并存入Redis缓存
 */
@Component
public class HotTopicTask {
    
    @Resource
    private TopicMapper topicMapper;
    
    @Resource
    private UserMapper userMapper;
    
    @Resource
    private ForumMapper forumMapper;
    
    @Resource
    private RedisUtils redisUtils;
    
    /**
     * 每10分钟执行一次，计算热门帖子
     * 排序算法：score = log(max(1, view_count)) + like_count * 2 + reply_count * 3 + time_decay
     */
    @Scheduled(fixedRate = 600000)  // 10分钟 = 600000毫秒
    public void calculateHotTopics() {
        // 查询所有正常状态的帖子
        LambdaQueryWrapper<Topic> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Topic::getStatus, 1)
                .orderByDesc(Topic::getIsTop)
                .orderByDesc(Topic::getIsEssence)
                .orderByDesc(Topic::getReplyCount)
                .orderByDesc(Topic::getViewCount);
        List<Topic> topics = topicMapper.selectList(wrapper);
        
        // 转换为VO并限制数量
        List<TopicVO> hotTopics = topics.stream()
                .limit(50)  // 只取前50个
                .map(this::getTopicVO)
                .collect(Collectors.toList());
        
        // 存入Redis缓存（10分钟过期）
        redisUtils.setEx("hot:topics", hotTopics, 600);
    }
    
    /**
     * 将Topic实体转换为TopicVO（简化版，用于热门列表）
     */
    private TopicVO getTopicVO(Topic topic) {
        TopicVO vo = new TopicVO();
        vo.setId(topic.getId());
        vo.setForumId(topic.getForumId());
        vo.setUserId(topic.getUserId());
        vo.setTitle(topic.getTitle());
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