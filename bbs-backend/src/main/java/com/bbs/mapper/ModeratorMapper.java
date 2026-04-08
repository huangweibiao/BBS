package com.bbs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bbs.entity.Moderator;
import org.apache.ibatis.annotations.Mapper;

/**
 * 版主Mapper接口
 */
@Mapper
public interface ModeratorMapper extends BaseMapper<Moderator> {
}