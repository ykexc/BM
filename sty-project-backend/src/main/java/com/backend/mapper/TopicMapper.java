package com.backend.mapper;

import com.backend.entity.dto.Topic;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author mqz
 */
@Mapper
public interface TopicMapper extends BaseMapper<Topic> {
}
