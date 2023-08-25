package com.backend.entity.vo.req;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * @author mqz
 */
@Data
@AllArgsConstructor
public class ResetConfirmVo {

    @Email
    String email;


    @Length(min = 6, max = 6)
    String code;

}
