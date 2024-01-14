package com.backend.entity.vo.req;

import com.alibaba.fastjson2.JSONObject;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * @author mqz
 */
@Data
public class TopicCreateVo {

    @Min(1)
    Integer type;

    @Length(min = 1, max = 30)
    String title;

    JSONObject content;

}
