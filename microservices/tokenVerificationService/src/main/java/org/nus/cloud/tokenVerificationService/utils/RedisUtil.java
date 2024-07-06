package org.nus.cloud.tokenVerificationService.utils;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public String verifyToken(String userToken, boolean resetExpireTime, boolean delete) {
        if (userToken == null) {
            return null;
        } else {
            String value;
            if (resetExpireTime) {
                long timeout = 3600;
                TimeUnit timeUnit = TimeUnit.SECONDS;
                stringRedisTemplate.expire(userToken, timeout, timeUnit);
                value = stringRedisTemplate.opsForValue().get(userToken);
                if (value != null) {
                    stringRedisTemplate.opsForValue().set(value, userToken, timeout, timeUnit);
                }
            }
            else {
                value = stringRedisTemplate.opsForValue().get(userToken);
            }
            if (value == null) {
                return null;
            }
            if (delete) {
                stringRedisTemplate.delete(userToken);
            }
            return value;
        }
    }
}
