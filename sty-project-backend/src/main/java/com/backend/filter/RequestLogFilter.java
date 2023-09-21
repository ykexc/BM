package com.backend.filter;

import com.alibaba.fastjson2.JSONObject;
import com.backend.utils.Const;
import com.backend.utils.SnowflakeIdGenerator;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.Set;

/**
 * @author mqz
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RequestLogFilter extends OncePerRequestFilter {


    private final SnowflakeIdGenerator snowflakeIdGenerator;

    private final Set<String> IGNORE = Set.of("/images");

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain)
            throws ServletException, IOException {
        if (checkIsIgnore(request.getServletPath())) {
            filterChain.doFilter(request, response);
            return;
        }
        long startTime = System.currentTimeMillis();
        logRequestStart(request);
        ContentCachingResponseWrapper wrapper = new ContentCachingResponseWrapper(response);
        filterChain.doFilter(request, wrapper);
        logRequestEnd(wrapper, startTime);
        wrapper.copyBodyToResponse();
    }


    public void logRequestStart(HttpServletRequest request) {
        long reqId = snowflakeIdGenerator.nextId();
        MDC.put("reqId", String.valueOf(reqId));
        JSONObject object = new JSONObject();
        request.getParameterMap().forEach((k, v) -> object.put(k, v.length > 0 ? v[0] : null));
        Object id = request.getAttribute(Const.ATTR_USER_ID);
        if (id != null) {
            UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            log.info("请求URL: \"{}\" ({}) | 远程IP地址: {} | 身份: {} (UID: {}) | 角色: {} | 请求参数列表: {}",
                    request.getServletPath(), request.getMethod(), request.getRemoteAddr(), principal.getUsername(), id,
                    principal.getAuthorities(), object);
        } else {
            log.info("请求URL: \"{}\" ({}) | 远程ip地址: {} | 身份: 未验证 | 请求参数列表: {}",
                    request.getServletPath(), request.getMethod(), request.getRemoteAddr(), object);
        }
    }

    public void logRequestEnd(ContentCachingResponseWrapper wrapper, long startTime) {
        long time = System.currentTimeMillis() - startTime;
        int status = wrapper.getStatus();
        String content = status != 200 ? status + "错误" : new String(wrapper.getContentAsByteArray());
        log.info("请求处理耗时: {}ms | 响应结果: {}", time, content);
    }

    private boolean checkIsIgnore(String url) {
        for (var ignore : IGNORE) {
            if (ignore.startsWith(url)) return true;
        }
        return false;
    }
}
