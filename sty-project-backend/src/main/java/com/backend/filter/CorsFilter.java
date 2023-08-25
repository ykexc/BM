package com.backend.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.backend.utils.Const.ORDER_CORS;


/**
 * @author mqz
 */
@Component
@Order(ORDER_CORS)
public class CorsFilter extends HttpFilter {


    @Value("${spring.web.cors.origin}")
    String origin;

    @Value("${spring.web.cors.credentials}")
    boolean credentials;

    @Value("${spring.web.cors.methods}")
    String methods;

    @Override
    protected void doFilter(HttpServletRequest request,
                            HttpServletResponse response,
                            FilterChain chain
    ) throws ServletException, IOException {
        addCorsHeader(request, response);
        chain.doFilter(request, response);
    }

    private void addCorsHeader(HttpServletRequest request, HttpServletResponse response) {
        response.addHeader("Access-Control-Allow-Origin", resolveOrigin(request));
        response.addHeader("Access-Control-Allow-Methods", resolveMethod());
        response.addHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
        if (credentials) response.addHeader("Access-Control-Allow-Credentials", "true");
    }


    private String resolveMethod() {
        return methods.equals("*") ? "GET, HEAD, POST, PUT, DELETE, OPTIONS, TRACE, PATCH" : methods;
    }

    private String resolveOrigin(HttpServletRequest request) {
        return origin.equals("*") ? request.getHeader("Origin") : origin;
    }
}
