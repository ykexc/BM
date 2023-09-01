package com.backend.service;

import com.backend.entity.dto.AccountDetails;
import com.backend.entity.vo.req.DetailsSaveVo;
import com.backend.entity.vo.req.EmailSaveVo;
import com.backend.entity.vo.req.SaveAccountPrivacyVo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author mqz
 */
public interface AccountDetailsService extends IService<AccountDetails> {

    boolean saveAccountDetails(Integer id, DetailsSaveVo detailsSaveVo);


    AccountDetails findAccountDetailsById(Integer id);

}
