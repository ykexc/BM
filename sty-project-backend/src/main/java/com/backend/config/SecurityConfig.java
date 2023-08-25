package com.backend.config;

import com.backend.entity.RestBean;
import com.backend.entity.dto.Account;
import com.backend.entity.dto.UserAccount;
import com.backend.entity.vo.resp.AuthorizeVo;
import com.backend.filter.JWTAuthenticationFilter;
import com.backend.filter.RequestLogFilter;
import com.backend.service.AccountService;
import com.backend.utils.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

/**
 * @author mqz
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {


    private final JWTUtil jwtUtil;

    private final JWTAuthenticationFilter jwtAuthenticationFilter;

    private final AccountService accountService;

    private final RequestLogFilter requestLogFilter;


    @Bean
    public UserDetailsService userDetails() {
        return userNameOrEmail -> {
            Account account = accountService.findUserByUserNameOrEmail(userNameOrEmail);
            if (account == null) throw new UsernameNotFoundException("用户名或密码错误");
            return UserAccount.builder().account(account).build();
        };
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http

                .authorizeHttpRequests(e -> e
                        .requestMatchers("/api/auth/**")
                        .permitAll()
                        .anyRequest()
                        .authenticated()
                )
                .formLogin(e -> e
                        .loginProcessingUrl("/api/auth/login")
                        .successHandler(this::onAuthenticationSuccess)
                        .failureHandler(this::onAuthenticationFailure)
                        .permitAll()
                )
                .logout(e -> e
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler(this::onLogoutSuccess)
                        .permitAll()
                )
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(this::commence)
                        .accessDeniedHandler(this::handle)
                )
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(e -> e
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(requestLogFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, RequestLogFilter.class)
                .build();
    }


    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication
    ) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        var writer = response.getWriter();
        UserDetails user = (UserDetails) authentication.getPrincipal();
        Account account = accountService.findUserByUserNameOrEmail(user.getUsername());
        String jwt = jwtUtil.generalToken(user, user.getUsername(), account.getId());
        if (jwt == null) writer.write(RestBean.forbidden("登录验证频繁, 请稍后再试").asJsonString());
        else {
            log.info("成功");
            AuthorizeVo vo = account.asViewObject(AuthorizeVo.class, e -> e.setToken(jwt));
            vo.setExpire(jwtUtil.expireTime());
            writer.write(RestBean.success(vo).asJsonString());
        }
    }

    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException
    ) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        var writer = response.getWriter();
        writer.write(RestBean.forbidden(accessDeniedException.getMessage()).asJsonString());
    }

    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception
    ) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        var writer = response.getWriter();
        writer.write(RestBean.unauthorized(exception.getMessage()).asJsonString());
    }

    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException
    ) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        var writer = response.getWriter();
        writer.write(RestBean.unauthorized(authException.getMessage()).asJsonString());
    }

    public void onLogoutSuccess(HttpServletRequest request,
                                HttpServletResponse response,
                                Authentication authentication
    ) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        var writer = response.getWriter();
        boolean invalidated = jwtUtil.invalidateJwt(request.getHeader("Authorization"));
        if (invalidated) {
            writer.write(RestBean.success("退出登录成功").asJsonString());
            return;
        }
        log.info("hello-world");
        writer.write(RestBean.failure(400, "退出登录成功").asJsonString());
    }

}
