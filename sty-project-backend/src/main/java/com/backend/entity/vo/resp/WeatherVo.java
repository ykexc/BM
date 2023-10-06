package com.backend.entity.vo.resp;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

/**
 * @author mqz
 */
@Data
public class WeatherVo {

    JSONObject location;

    JSONObject now;

    JSONArray hourly;


}
