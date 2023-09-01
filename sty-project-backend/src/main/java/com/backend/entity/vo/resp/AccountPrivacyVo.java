package com.backend.entity.vo.resp;

import lombok.Data;

/**
 * @author mqz
 */
@Data
public class AccountPrivacyVo {

    Boolean phone = true;

    Boolean email = true;

    Boolean wx = true;

    Boolean qq = true;

    Boolean gender = true;

}
