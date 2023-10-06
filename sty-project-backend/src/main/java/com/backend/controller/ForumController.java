package com.backend.controller;

import com.backend.entity.RestBean;
import com.backend.entity.vo.resp.WeatherVo;
import com.backend.service.WeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author mqz
 */
@RestController
@RequestMapping("/api/forum")
@RequiredArgsConstructor
@Slf4j
public class ForumController {


    private final WeatherService weatherService;

    @GetMapping("/weather")
    public RestBean<WeatherVo> weather(double longitude, double latitude) {
        WeatherVo vo = weatherService.fetchWeather(longitude, latitude);
        return vo == null ? RestBean.failure(400, "获取地理信息错误") : RestBean.success(vo);
    }

}
