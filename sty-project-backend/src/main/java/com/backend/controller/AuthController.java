package com.backend.controller;

import com.backend.entity.RestBean;
import com.backend.entity.vo.req.EmailRegisterVo;
import com.backend.entity.vo.req.EmailResetVo;
import com.backend.entity.vo.req.ResetConfirmVo;
import com.backend.service.AccountService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author mqz
 */

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {


    private final AccountService accountService;


    @GetMapping
    public RestBean<String> test() {
        return RestBean.success("huay");
    }


    @GetMapping("/ask-code")
    public RestBean<Void> askVerifyCode(
            @RequestParam String email,
            @RequestParam String type,
            HttpServletRequest request
    ) {
        return this.messageHandle(() -> accountService.registerOrResetEmailVerifyCode(email, type, request.getRemoteAddr()));
    }

    @PostMapping("/register")
    public RestBean<Void> register(@RequestBody @Valid EmailRegisterVo vo) {
        return handle(vo, accountService::registerEmailAccount);
    }


    @PostMapping("/reset-confirm")
    public RestBean<Void> resetConfirm(
            @RequestBody @Valid ResetConfirmVo resetConfirmVo
            ) {
        return handle(resetConfirmVo, accountService::resetConfirm);
    }


    @PostMapping("/reset")
    public RestBean<Void> reset(
            @RequestBody @Valid EmailResetVo resetVo
            ) {
        return handle(resetVo, accountService::emailReset);
    }


    private <T> RestBean<Void> handle(T vo, Function<T, String> function) {
        return messageHandle(() -> function.apply(vo));
    }

    private <T> RestBean<T> messageHandle(Supplier<String> action) {
        String t = action.get();
        if (t == null) return RestBean.success();
        return RestBean.failure(400, t);
    }

}
