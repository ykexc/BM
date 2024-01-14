package com.backend.controller;

import com.backend.entity.RestBean;
import com.backend.entity.vo.req.TopicCreateVo;
import com.backend.entity.vo.resp.TopicTypeVo;
import com.backend.entity.vo.resp.WeatherVo;
import com.backend.service.TopicService;
import com.backend.service.WeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.backend.utils.Const.ATTR_USER_ID;

/**
 * @author mqz
 */
@RestController
@RequestMapping("/api/forum")
@RequiredArgsConstructor
@Slf4j
public class ForumController {


    private final WeatherService weatherService;
    private final TopicService topicService;

    @GetMapping("/weather")
    public RestBean<WeatherVo> weather(double longitude, double latitude) {
        WeatherVo vo = weatherService.fetchWeather(longitude, latitude);
        return vo == null ? RestBean.failure(400, "获取地理信息错误") : RestBean.success(vo);
    }


    @GetMapping("/topic/type")
    public RestBean<List<TopicTypeVo>> topicTypes() {
        return RestBean.success(
                topicService
                        .allTopicTypes()
                        .stream()
                        .map(topicType -> topicType.asViewObject(TopicTypeVo.class))
                        .toList()
        );
    }


    @PostMapping("/topic/create")
    public RestBean<Void> createTopic(
            @RequestBody TopicCreateVo topicCreateVo,
            @RequestAttribute(ATTR_USER_ID) Integer id
            ) {
        String res = topicService.createTopic(topicCreateVo, id);
        return res == null ? RestBean.success() : RestBean.failure(505, res);
    }

}
