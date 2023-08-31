package com.backend.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.backend.utils.Const.JWT_BLACK_LIST;

/**
 * @author mqz
 */
@Component
@RequiredArgsConstructor
public class JWTUtil {

    private final StringRedisTemplate redisTemplate;

    private final FlowUtil flowUtil;

    @Value("${spring.security.jwt.key}")
    private String key;

    @Value("${spring.security.jwt.expire}")
    private int expire;

    @Value("${spring.security.jwt.limit.base}")
    private int limitBase;

    @Value("${spring.security.jwt.limit.upgrade}")
    private int limitUpgrade;

    @Value("${spring.security.jwt.limit.freq}")
    private int limitFreq;


    private Date getExpireTime() {
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.HOUR, expire);
        return instance.getTime();
    }

    public boolean invalidateJwt(String headerToken) {
        String convertToken = convertToken(headerToken);
        Algorithm algorithm = Algorithm.HMAC256(key);
        JWTVerifier verifier = JWT.require(algorithm).build();
        try {
            DecodedJWT verify = verifier.verify(convertToken);
            return deleteToken(verify.getId(), verify.getExpiresAt());
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    private String convertToken(String headerToken) {
        if (headerToken == null || !headerToken.startsWith("Bearer "))
            return null;
        return headerToken.substring(7);
    }


    public String generalToken(UserDetails user, String username, int userId) {
        if (frequencyCheck(userId)) {
            Algorithm algorithm = Algorithm.HMAC256(key);
            Date expireTime = getExpireTime();
            return JWT.create()
                    .withJWTId(UUID.randomUUID().toString())
                    .withClaim("id", userId)
                    .withClaim("name", username)
                    .withClaim("authorities", user
                            .getAuthorities()
                            .stream()
                            .map(GrantedAuthority::getAuthority)
                            .toList())
                    .withExpiresAt(expireTime)
                    .sign(algorithm);
        }
        return null;
    }


    public DecodedJWT resolveToken(String headerToken) {
        String token = convertToken(headerToken);
        if (token == null) return null;
        Algorithm algorithm = Algorithm.HMAC256(key);
        JWTVerifier verifier = JWT.require(algorithm).build();
        try {
            DecodedJWT verify = verifier.verify(token);
            if (isInvalidToken(verify.getId())) return null;
            Map<String, Claim> claims = verify.getClaims();
            return new Date().after(claims.get("exp").asDate()) ? null : verify;
        } catch (JWTVerificationException e) {
            return null;
        }
    }

    private boolean isInvalidToken(String id) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(JWT_BLACK_LIST + id));
    }

    private boolean deleteToken(String tokenId, Date expireAt) {
        if (isInvalidToken(tokenId)) return false;
        Date now = new Date();
        long expire = Math.max(0, expireAt.getTime() - now.getTime());
        redisTemplate.opsForValue().set(JWT_BLACK_LIST + tokenId, "", expire, TimeUnit.MILLISECONDS);
        return true;
    }

    public UserDetails toUser(DecodedJWT jwt) {
        Map<String, Claim> claims = jwt.getClaims();
        return User
                .withUsername(claims.get("name").asString())
                .password("******").authorities(claims.get("authorities").asArray(String.class))
                .build();
    }

    public Integer toId(DecodedJWT decodedJWT) {
        return decodedJWT.getClaims().get("id").asInt();
    }

    public Long getExpireTimeMilliseconds() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, expire);
        return calendar.getTime().getTime();
    }

    private boolean frequencyCheck(Integer userId) {
        String key = Const.JWT_FREQUENCY + userId;
        return flowUtil.limitOnceUpgradeCheck(key, limitFreq, limitBase, limitUpgrade);
    }
}
