package com.backend.service.impl;

import com.backend.entity.dto.AccountPrivacy;
import com.backend.entity.vo.req.SaveAccountPrivacyVo;
import com.backend.mapper.AccountPrivacyMapper;
import com.backend.service.AccountPrivacyService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * @author mqz
 */
@Service
public class AccountPrivacyServiceImpl extends ServiceImpl<AccountPrivacyMapper, AccountPrivacy> implements AccountPrivacyService {


    @Override
    @Transactional
    public String saveAccountPrivacy(Integer id, SaveAccountPrivacyVo vo) {
        AccountPrivacy accountPrivacy = Optional.ofNullable(this.getById(id)).orElseGet(AccountPrivacy::new);
        accountPrivacy.setId(id);
        Boolean flag = vo.getFlag();
        switch (vo.getType()) {
            case "phone" -> accountPrivacy.setPhone(flag);
            case "email" -> accountPrivacy.setEmail(flag);
            case "qq" -> accountPrivacy.setQq(flag);
            case "wx" -> accountPrivacy.setWx(flag);
            case "gender" -> accountPrivacy.setGender(flag);
        }
        boolean saveOrUpdate = this.saveOrUpdate(accountPrivacy);
        return saveOrUpdate ? null : "更新失败,请联系管理员";
    }

    @Override
    public AccountPrivacy getAccountPrivacy(Integer id) {
        return Optional.ofNullable(this.getById(id)).orElseGet(AccountPrivacy::new);
    }
}
