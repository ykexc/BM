package com.backend.service;

import com.backend.entity.dto.AccountPrivacy;
import com.backend.entity.vo.req.SaveAccountPrivacyVo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author mqz
 */
public interface AccountPrivacyService extends IService<AccountPrivacy> {

    String saveAccountPrivacy(Integer id, SaveAccountPrivacyVo vo);

    AccountPrivacy getAccountPrivacy(Integer id);

}
