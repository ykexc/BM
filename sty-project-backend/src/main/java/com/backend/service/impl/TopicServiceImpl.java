package com.backend.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.backend.entity.dto.Topic;
import com.backend.entity.dto.TopicType;
import com.backend.entity.vo.req.TopicCreateVo;
import com.backend.mapper.TopicMapper;
import com.backend.mapper.TopicTypeMapper;
import com.backend.service.TopicService;
import com.backend.utils.FlowUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.backend.utils.Const.FORUM_CREATE_TOPIC;

/**
 * @author mqz
 */
@RequiredArgsConstructor
@Service
public class TopicServiceImpl extends ServiceImpl<TopicMapper, Topic> implements TopicService {

    final TopicTypeMapper topicTypeMapper;

    final FlowUtil flowUtil;

    /**
     * @return List&lt TopicType &gt
     */
    @Override
    public List<TopicType> allTopicTypes() {
        return topicTypeMapper.selectList(null);
    }

    /**
     * @param topicCreateVo topicCreateVo
     * @param id            userId
     * @return error_msg
     */
    @Override
    @Transactional
    public String createTopic(TopicCreateVo topicCreateVo, Integer id) {
        Set<Integer> collect = topicTypeMapper
                .selectList(null)
                .stream()
                .map(TopicType::getId)
                .collect(Collectors.toSet());
        System.out.println(topicCreateVo.getType());
        if (!collect.contains(topicCreateVo.getType()))
            return "错误的类型";
        if (!checkIsValidContent(topicCreateVo.getContent()))
            return "不符合文章的字数限制";
        boolean isLimit = flowUtil.limitPeriodCounterCheck(FORUM_CREATE_TOPIC + id, 3, 3600);
        if (!isLimit) return "频繁发文!";
        Topic topic = new Topic();
        BeanUtils.copyProperties(topicCreateVo, topic);
        topic.setUid(id);
        topic.setContent(topicCreateVo.getContent().toJSONString());
        topic.setTime(new Date());
        if (!this.save(topic)) return "内部错误";
        return null;
    }

    private boolean checkIsValidContent(JSONObject object) {
        if (object == null) return false;
        int cnt = 0;
        JSONArray ops = object.getJSONArray("ops");
        for (var s : ops) {
            cnt += JSONObject.from(s).getString("insert").length();
            if (cnt > 20000) return false;
        }
        return true;
    }
}
