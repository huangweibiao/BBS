package com.bbs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bbs.entity.Topic;
import org.apache.ibatis.annotations.Mapper;

/**
 * 帖子Mapper接口
 */
@Mapper
public interface TopicMapper extends BaseMapper<Topic> {
}