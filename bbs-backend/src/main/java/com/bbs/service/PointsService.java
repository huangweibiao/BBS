package com.bbs.service;

import com.bbs.dto.UserVO;

/**
 * 积分服务接口
 */
public interface PointsService {
    
    /**
     * 积分变动
     * @param userId 用户ID
     * @param pointsChange 积分变动值（正增负减）
     * @param actionType 操作类型
     * @param description 描述
     */
    void changePoints(Long userId, Integer pointsChange, Integer actionType, String description);
    
    /**
     * 获取用户积分
     */
    Integer getUserPoints(Long userId);
    
    /**
     * 获取用户积分记录
     */
    // List<PointsLogVO> getPointsLogs(Long userId, Integer pageNum, Integer pageSize);
}