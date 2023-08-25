package com.backend.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

/**
 * @author mqz
 */
@Component
@RabbitListener(queues = "mail")
@RequiredArgsConstructor
public class MailQueueListener {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    String username;


    @RabbitHandler
    public void sendMailMessage(Map<String, ? extends Serializable> data) {
        String mail = data.get("email").toString();
        Integer code = (Integer) data.get("code");
        SimpleMailMessage msg = switch (data.get("type").toString()) {
            case "register" -> createMsg("欢迎注册我们的网站",
                    "您的邮件注册验证码为: " + code + "，有效时间3分钟，为了保障您的账户安全，请勿向他人泄露验证码信息。",
                    mail);
            case "reset" -> createMsg("您的密码重置邮件",
                    "你好，您正在执行重置密码操作，验证码: " + code + "，有效时间3分钟，如非本人操作，请无视。",
                    mail);
            default -> null;
        };
        assert msg != null;
        javaMailSender.send(msg);
    }

    private SimpleMailMessage createMsg(String title, String content, String email) {
        return new SimpleMailMessage() {{
            setSubject(title);
            setText(content);
            setTo(email);
            setFrom(username);
        }};
    }
}
