package com.backend.entity.vo.resp;

import lombok.Data;

import java.util.Date;

/**
 * @author mqz
 */
@Data
public class AccountVo {

    String username;

    String email;

    String role;

    Date registerTime;

    String avatar;

}
