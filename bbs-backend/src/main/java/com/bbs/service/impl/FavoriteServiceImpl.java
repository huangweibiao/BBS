package com.bbs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bbs.common.BusinessException;
import com.bbs.dto.FavoriteRequestDTO;
import com.bbs.entity.Favorite;
import com.bbs.entity.Topic;
import com.bbs.mapper.FavoriteMapper;
import com.bbs.mapper.TopicMapper;
import com.bbs.service.FavoriteService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 收藏服务实现类
 */
@Service
public class FavoriteServiceImpl implements FavoriteService {
    
    @Resource
    private FavoriteMapper favoriteMapper;
    
    @Resource
    private TopicMapper topicMapper;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String toggleFavorite(FavoriteRequestDTO request, Long userId) {
        // 检查帖子是否存在
        Topic topic = topicMapper.selectById(request.getPostId());
        if (topic == null || topic.getStatus() != 1) {
            throw new BusinessException("帖子不存在");
        }
        
        // 检查是否已收藏
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
                .eq(Favorite::getPostId, request.getPostId());
        
        Favorite existingFavorite = favoriteMapper.selectOne(wrapper);
        
        if (existingFavorite != null) {
            // 取消收藏
            favoriteMapper.deleteById(existingFavorite.getId());
            return "取消收藏成功";
        } else {
            // 收藏
            Favorite favorite = new Favorite();
            favorite.setUserId(userId);
            favorite.setPostId(request.getPostId());
            favorite.setCreatedAt(LocalDateTime.now());
            
            favoriteMapper.insert(favorite);
            return "收藏成功";
        }
    }
    
    @Override
    public boolean hasFavorited(Long userId, Long postId) {
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
                .eq(Favorite::getPostId, postId);
        
        return favoriteMapper.selectCount(wrapper) > 0;
    }
}