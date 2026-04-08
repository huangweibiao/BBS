package com.bbs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bbs.entity.Reply;
import org.apache.ibatis.annotations.Mapper;

/**
 * 回复Mapper接口
 */
@Mapper
public interface ReplyMapper extends BaseMapper<Reply> {
}