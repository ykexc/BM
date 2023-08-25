package com.backend.entity.vo.resp;

import lombok.Data;

import java.util.Date;

/**
 * @author mqz
 */
@Data
public class AuthorizeVo {

    String username;

    String role;

    String token;

    Date expire;

}
