package com.backend.entity.vo.req;

import jakarta.validation.constraints.Email;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * @author mqz
 */
@Data
public class EmailSaveVo {

    @Email
    private String email;

    @Length(max = 6, min = 6)
    private String code;

}
