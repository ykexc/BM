package com.backend.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.backend.entity.vo.resp.WeatherVo;
import com.backend.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

/**
 * @author mqz
 */
@Service
@RequiredArgsConstructor
public class WeatherServiceImpl implements WeatherService {

    private final RestTemplate restTemplate;

    private final StringRedisTemplate redisTemplate;

    @Value("${spring.weather.key}")
    String key;

    @Override
    public WeatherVo fetchWeather(double longitude, double latitude) {
        return fetchFromCache(longitude, latitude);
    }


    private WeatherVo fetchFromCache(double longitude, double latitude) {
        byte[] data = restTemplate.getForObject(
                "https://geoapi.qweather.com/v2/city/lookup?location=" + longitude + "," + latitude + "&key=" + key,
                byte[].class);
        JSONObject geo = decompressStringToJson(data);
        if (geo == null) return null;
        JSONObject location = geo.getJSONArray("location").getJSONObject(0);
        int id = location.getInteger("id");
        String key = "weather:" + id;
        String cache = redisTemplate.opsForValue().get(key);
        if (cache != null) {
            return JSONObject.parseObject(cache, WeatherVo.class);
        }
        WeatherVo vo = this.fetchWeather(id, location);
        redisTemplate.opsForValue().set(key, JSONObject.toJSONString(vo), 1, TimeUnit.HOURS);
        return vo;
    }


    private WeatherVo fetchWeather(int id, JSONObject location) {
        WeatherVo vo = new WeatherVo();
        vo.setLocation(location);
        byte[] nowByte = restTemplate.getForObject("https://devapi.qweather.com/v7/weather/now?location=" + id + "&key=" + key
                , byte[].class);
        JSONObject now = decompressStringToJson(nowByte);
        if (now == null) return null;
        vo.setNow(now);

        byte[] hourByte = restTemplate.getForObject("https://devapi.qweather.com/v7/weather/24h?location=" + id + "&key=" + key
                , byte[].class);
        JSONObject hourly = decompressStringToJson(hourByte);
        if (hourly == null) return null;
        vo.setHourly(new JSONArray(hourly.getJSONArray("hourly").stream().limit(5).toList()));
        return vo;
    }

    private JSONObject decompressStringToJson(byte[] data) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            GZIPInputStream inputStream = new GZIPInputStream(new ByteArrayInputStream(data));
            byte[] buffer = new byte[1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                stream.write(buffer, 0, read);
            }
            inputStream.close();
            stream.close();
            return JSONObject.parseObject(stream.toString());
        } catch (IOException e) {
            return null;
        }
    }


}
