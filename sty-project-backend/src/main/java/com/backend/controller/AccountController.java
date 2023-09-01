package com.backend.controller;

import com.backend.entity.RestBean;
import com.backend.entity.dto.AccountDetails;
import com.backend.entity.vo.req.ChangePasswordVo;
import com.backend.entity.vo.req.DetailsSaveVo;
import com.backend.entity.vo.req.EmailSaveVo;
import com.backend.entity.vo.resp.AccountDetailsVo;
import com.backend.entity.vo.resp.AccountVo;
import com.backend.service.AccountDetailsService;
import com.backend.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.function.Supplier;

import static com.backend.utils.Const.ATTR_USER_ID;
import static com.backend.utils.Const.NULL;

/**
 * @author mqz
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Validated
public class AccountController {

    private final AccountService accountService;

    private final AccountDetailsService accountDetailsService;

    @GetMapping("/info")
    public RestBean<AccountVo> userInfo(@RequestAttribute(ATTR_USER_ID) Integer id) {
        return RestBean.success(accountService.findUserById(id).asViewObject(AccountVo.class));
    }


    @GetMapping("/details")
    public RestBean<AccountDetailsVo> details(@RequestAttribute(ATTR_USER_ID) Integer id) {
        AccountDetails details = Optional.ofNullable(accountDetailsService.findAccountDetailsById(id)).orElseGet(AccountDetails::new);
        return RestBean.success(details.asViewObject(AccountDetailsVo.class));
    }

    @PostMapping("/save-details")
    public RestBean<Void> saveDetails(@RequestAttribute(ATTR_USER_ID) Integer id, @RequestBody @Valid DetailsSaveVo vo) {
        boolean success = accountDetailsService.saveAccountDetails(id, vo);
        return success ? RestBean.success() : RestBean.failure(400, "此用户名已被其他用户使用, 请重新更换");
    }

    @PostMapping("/save-email")
    public RestBean<Void> saveEmail(@RequestAttribute(ATTR_USER_ID) Integer id, @RequestBody @Valid EmailSaveVo vo) {
        return messageHandle(() -> accountService.saveAccountEmail(id, vo));
    }

    @PostMapping("/change-password")
    public RestBean<Void> changePassword(
            @RequestAttribute(ATTR_USER_ID) Integer id,
            @RequestBody @Valid ChangePasswordVo vo) {
        return messageHandle(() -> accountService.changePassword(id, vo));
    }


    private <T> RestBean<T> messageHandle(Supplier<String> action) {
        String t = action.get();
        if (t == NULL) return RestBean.success();
        return RestBean.failure(400, t);
    }

}
