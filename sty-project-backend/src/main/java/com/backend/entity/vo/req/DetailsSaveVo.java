package com.backend.entity.vo.req;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

/**
 * @author mqz
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetailsSaveVo {

    @Pattern(regexp = "^[a-zA-Z0-9\\u4e00-\\u9fa5]+$")
    @Length(max = 10, min = 1)
    String username;

    @Min(0)
    @Max(1)
    Integer gender;

    @Length(max = 11)
    String phone;

    @Length(max = 13)
    String qq;

    @Length(max = 20)
    String wx;

    @Length(max = 400)
    String desc;

}
