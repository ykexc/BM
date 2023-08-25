package com.backend.entity.vo.req;

import jakarta.validation.constraints.Email;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * @author mqz
 */
@Data
public class EmailResetVo {

    @Email
    String email;

    @Length(min = 6, max = 6)
    String code;

    @Length(min = 6, max = 20)
    String password;

}
