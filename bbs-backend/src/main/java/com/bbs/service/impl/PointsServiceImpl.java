package com.bbs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bbs.common.BusinessException;
import com.bbs.common.RedisUtils;
import com.bbs.entity.PointsLog;
import com.bbs.entity.User;
import com.bbs.mapper.PointsLogMapper;
import com.bbs.mapper.UserMapper;
import com.bbs.service.PointsService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 积分服务实现类
 */
@Service
public class PointsServiceImpl implements PointsService {
    
    @Resource
    private PointsLogMapper pointsLogMapper;
    
    @Resource
    private UserMapper userMapper;
    
    @Resource
    private RedisUtils redisUtils;
    
    // 积分规则常量
    public static final int POINTS_CREATE_TOPIC = 5;      // 发帖+5
    public static final int POINTS_CREATE_REPLY = 2;       // 回帖+2
    public static final int POINTS_ESSENCE = 10;           // 加精+10
    public static final int POINTS_TOP = 5;                // 置顶+5
    public static final int POINTS_SIGN = 1;               // 签到+1
    public static final int POINTS_LIKED = 1;              // 被点赞+1（每10次+1）
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePoints(Long userId, Integer pointsChange, Integer actionType, String description) {
        // 获取用户
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 更新用户积分
        user.setPoints(user.getPoints() + pointsChange);
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
        
        // 记录积分变动日志
        PointsLog log = new PointsLog();
        log.setUserId(userId);
        log.setPointsChange(pointsChange);
        log.setActionType(actionType);
        log.setDescription(description);
        log.setCreatedAt(LocalDateTime.now());
        
        pointsLogMapper.insert(log);
        
        // 清除用户缓存
        redisUtils.delete("user:" + userId);
    }
    
    @Override
    public Integer getUserPoints(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return user.getPoints();
    }
}