package com.backend.service;

import com.backend.entity.dto.Account;
import com.backend.entity.vo.req.*;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author mqz
 */
public interface AccountService extends IService<Account> {


    Account findUserByUserNameOrEmail(String text);

    Account findUserById(Integer id);

    String registerOrResetEmailVerifyCode(String email, String type, String ip);

    String registerEmailAccount(EmailRegisterVo info);

    String resetConfirm(ResetConfirmVo resetConfirmVo);

    String emailReset(EmailResetVo resetVo);

    String saveAccountEmail(Integer id, EmailSaveVo emailSaveVo);

    String changePassword(Integer id, ChangePasswordVo vo);

}
