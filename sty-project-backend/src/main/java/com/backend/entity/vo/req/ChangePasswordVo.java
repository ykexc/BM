package com.backend.entity.vo.req;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * @author mqz
 */
@Data
public class ChangePasswordVo {

    @Length(min = 6, max = 20)
    private String password;

    @Length(min = 6, max = 20)
    private String newPassword;

}
