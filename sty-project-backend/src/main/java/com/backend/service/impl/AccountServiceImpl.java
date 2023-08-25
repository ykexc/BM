package com.backend.service.impl;

import com.backend.entity.dto.Account;
import com.backend.entity.vo.req.EmailRegisterVo;
import com.backend.entity.vo.req.EmailResetVo;
import com.backend.entity.vo.req.ResetConfirmVo;
import com.backend.mapper.AccountMapper;
import com.backend.service.AccountService;
import com.backend.utils.Const;
import com.backend.utils.FlowUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author mqz
 */
@RequiredArgsConstructor
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {


    private final StringRedisTemplate stringRedisTemplate;

    private final FlowUtil flowUtil;

    private final AmqpTemplate amqpTemplate;

    private final PasswordEncoder encoder;

    @Value("${spring.web.verify.mail-limit}")
    int verifyLimit;

    private final Random random = new Random();


    @Override
    public Account findUserByUserNameOrEmail(String text) {
        return query()
                .eq("username", text).or()
                .eq("email", text)
                .one();
    }

    @Override
    public String registerOrResetEmailVerifyCode(String email, String type, String ip) {
        synchronized (ip.intern()) {
            if (!this.verifyLimit(ip)) return "请求频繁, 请稍后再试";
            int code = random.nextInt(899999) + 100000;
            var map = Map.of("type", type, "email", email, "code", code);
            amqpTemplate.convertAndSend(Const.MQ_MAIL, map);
            stringRedisTemplate.opsForValue().set(Const.VERIFY_EMAIL_DATA + email, String.valueOf(code), 3, TimeUnit.MINUTES);
            return null;
        }
    }

    @Override
    public String registerEmailAccount(EmailRegisterVo info) {
        String email = info.getEmail(), code = info.getCode();
        String emailCode = getEmailCode(email);
        if (emailCode == null) return "请先获取验证码";
        if (!code.equals(emailCode)) return "验证码错误，请重新输入";
        if (existUser(email)) {
            deleteRegisterEmail(email);
            return "该邮件地址已被注册";
        }
        String username = info.getUsername();
        if (existUserByUsername(username)) return "该用户名已被他人使用，请重新更换";
        String password = encoder.encode(info.getPassword());
        Account account = new Account(null, username, password, email, Const.ROLE_DEFAULT, new Date());
        if (!save(account)) return "内部错误, 注册失败";
        deleteRegisterEmail(email);
        return null;
    }

    @Override
    public String resetConfirm(ResetConfirmVo resetConfirmVo) {
        String email = resetConfirmVo.getEmail();
        boolean existed = existUser(email);
        if (!existed) return "用户不存在";
        String emailCode = getEmailCode(email);
        if (emailCode == null) return "请先获取验证码";
        if (!emailCode.equals(resetConfirmVo.getCode())) return "验证码错误";
        return null;
    }

    @Override
    public String emailReset(EmailResetVo resetVo) {
        String email = resetVo.getEmail(), password = resetVo.getPassword(), code = resetVo.getCode();
        String ok = resetConfirm(new ResetConfirmVo(email, code));
        if (ok != null) return ok;
        boolean update = update().eq("email", email).set("password", encoder.encode(password )).update();
        if (update) deleteRegisterEmail(email);
        return update ? null : "更新失败,请联系管理员";
    }


    private boolean verifyLimit(String ip) {
        String key = Const.VERIFY_EMAIL_LIMIT + ip;
        return flowUtil.limitOnceCheck(key, verifyLimit);
    }

    private String getEmailCode(String email) {
        String key = Const.VERIFY_EMAIL_DATA + email;
        return stringRedisTemplate.opsForValue().get(key);
    }

    private boolean existUser(String email) {
        return baseMapper.exists(Wrappers.<Account>query().eq("email", email));
    }

    private boolean existUserByUsername(String username) {
        return baseMapper.exists(Wrappers.<Account>query().eq("username", username));
    }

    private void deleteRegisterEmail(String email) {
        String key = Const.VERIFY_EMAIL_DATA + email;
        stringRedisTemplate.delete(key);
    }
}
