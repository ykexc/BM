package com.backend.service.impl;

import com.backend.entity.dto.Account;
import com.backend.entity.dto.AccountDetails;
import com.backend.entity.vo.req.DetailsSaveVo;
import com.backend.mapper.AccountDetailsMapper;
import com.backend.service.AccountDetailsService;
import com.backend.service.AccountService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * @author mqz
 */
@Service
@RequiredArgsConstructor
public class AccountDetailsServiceImpl extends ServiceImpl<AccountDetailsMapper, AccountDetails> implements AccountDetailsService {


    private final AccountService accountService;

    @Override
    @Transactional
    public synchronized boolean saveAccountDetails(Integer id, DetailsSaveVo detailsSaveVo) {
        Account account = accountService.findUserByUserNameOrEmail(detailsSaveVo.getUsername());
        if (account == null || Objects.equals(account.getId(), id)) {
            accountService.update().eq("id", id).set("username", detailsSaveVo.getUsername())
                    .update();
            return this.saveOrUpdate(new AccountDetails(
                    id,
                    detailsSaveVo.getGender(),
                    detailsSaveVo.getPhone(),
                    detailsSaveVo.getQq(),
                    detailsSaveVo.getWx(),
                    detailsSaveVo.getDesc()
            ));
        }
        return false;
    }



    @Override
    public AccountDetails findAccountDetailsById(Integer id) {
        return this.getById(id);
    }
}
