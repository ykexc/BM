package com.backend.filter;

import com.backend.entity.RestBean;
import com.backend.utils.Const;
import com.backend.utils.FlowUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author mqz
 */
@Component
@RequiredArgsConstructor
@Order(Const.ORDER_FLOW_LIMIT)
public class FlowLimitFilter extends HttpFilter {

    private final StringRedisTemplate stringRedisTemplate;

    private final FlowUtil flowUtil;

    @Value("${spring.web.flow.limit}")
    private int limit;

    @Value("${spring.web.flow.period}")
    private int period;

    @Value("${spring.web.flow.block}")
    private int block;

    @Override
    protected void doFilter(HttpServletRequest request,
                            HttpServletResponse response,
                            FilterChain chain)
            throws IOException, ServletException {
        String ip = request.getRemoteAddr();
        if (!tryCount(ip))
            writeBlockMessage(response);
        else
            chain.doFilter(request, response);
    }

    private boolean tryCount(String address) {
        String counterKey = Const.FLOW_LIMIT_COUNTER + address;
        String blockKey = Const.FLOW_LIMIT_BLOCK;
        synchronized (address.intern()) {
            if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(blockKey + address)))
                return false;
            return flowUtil.limitPeriodCheck(counterKey, blockKey, block, limit, period);
        }
    }

    private void writeBlockMessage(HttpServletResponse resp) throws IOException {
        resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
        resp.setContentType("application/json;charset=utf-8");
        PrintWriter out = resp.getWriter();
        out.write(RestBean.forbidden("操作频繁,请稍后再试").asJsonString());
    }
}
