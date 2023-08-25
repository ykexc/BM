package com.backend.entity.vo.req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * @author mqz
 */
@Data
public class EmailRegisterVo {


    @Email
    String email;

    @Length(max = 6, min = 6)
    String code;

    @Pattern(regexp = "^[a-zA-Z0-9\\u4e00-\\u9fa5]+$")
    @Length(max = 10, min = 1)
    String username;
    @Length(min = 6, max = 20)
    String password;

}
