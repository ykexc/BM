package com.backend.service;

import com.backend.entity.dto.Topic;
import com.backend.entity.dto.TopicType;
import com.backend.entity.vo.req.TopicCreateVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author mqz
 */
public interface TopicService extends IService<Topic> {


    List<TopicType> allTopicTypes();


    String createTopic(TopicCreateVo topicCreateVo, Integer id);
}
