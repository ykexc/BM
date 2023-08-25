package com.backend.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author mqz
 */
@Component
@RequiredArgsConstructor
public class FlowUtil {


    private final StringRedisTemplate redisTemplate;

    /**
     * 限制行为策略
     */
    private interface LimitAction {
        boolean run(boolean overclock);
    }


    public boolean limitOnceCheck(String key, int blockTime) {
        return internalCheck(key, blockTime, e -> e);
    }

    /**
     * 单次频率限制,若持续请求则升级限制时间
     *
     * @param key         健
     * @param freq        请求频率
     * @param baseTime    基础限制时间
     * @param upgradeTime 升级限制时间
     * @return 是否通过限流
     */
    public boolean limitOnceUpgradeCheck(String key, int freq, int baseTime, int upgradeTime) {
        return this.internalCheck(key, freq, baseTime, isExceed -> {
            if (isExceed) redisTemplate.opsForValue().set(key, "1", upgradeTime, TimeUnit.SECONDS);
            return false;
        });
    }

    public boolean limitPeriodCheck(String counterKey, String blockKey, int blockTime, int freq, int period) {
        return this.internalCheck(counterKey, freq, period, isExceed -> {
            if (isExceed)
                redisTemplate.opsForValue().set(blockKey, "", blockTime, TimeUnit.SECONDS);
            return !isExceed;
        });
    }

    private boolean internalCheck(String key, int period, LimitAction action) {
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            return action.run(false);
        } else {
            redisTemplate.opsForValue().set(key, "1", period, TimeUnit.SECONDS);
            return true;
        }
    }

    private boolean internalCheck(String key, int freq, int period, LimitAction action) {
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            Long v = Optional.ofNullable(redisTemplate.opsForValue().increment(key)).orElse(0L);
            return action.run(v > freq);
        } else {
            redisTemplate.opsForValue().set(key, "1", period, TimeUnit.SECONDS);
            return true;
        }
    }

}
