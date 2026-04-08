package com.bbs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bbs.entity.Forum;
import org.apache.ibatis.annotations.Mapper;

/**
 * 版块Mapper接口
 */
@Mapper
public interface ForumMapper extends BaseMapper<Forum> {
}