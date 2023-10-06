package com.backend.service;

import com.backend.entity.vo.resp.WeatherVo;

/**
 * @author mqz
 */
public interface WeatherService {

    WeatherVo fetchWeather(double longitude, double latitude);

}
