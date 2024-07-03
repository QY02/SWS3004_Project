package org.eventCenter.fileServer.utils;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisUtil {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public String getAndDelete(String token) {
        if (token == null) {
            return null;
        } else {
            String value = stringRedisTemplate.opsForValue().get(token);
            if (value == null) {
                return null;
            }
            stringRedisTemplate.delete(token);
            return value;
        }
    }
}
