package org.nus.cloud.loginService.utils;

import jakarta.annotation.Resource;
import org.nus.cloud.loginService.entity.User;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    private final long timeout = 3600;
    private final TimeUnit timeUnit = TimeUnit.SECONDS;

    public void add(String value, String key) {
        if ((value != null) && (key != null)) {
            stringRedisTemplate.opsForValue().set(key, value, timeout, timeUnit);
            stringRedisTemplate.opsForValue().set(value, key, timeout, timeUnit);
        }
    }

    public String get(String userToken, boolean resetExpireTime, boolean delete) {
        if (userToken == null) {
            return null;
        } else {
            if (resetExpireTime) {
                stringRedisTemplate.expire(userToken, timeout, timeUnit);
            }
            String value = stringRedisTemplate.opsForValue().get(userToken);
            if (value == null) {
                return null;
            }
            if (delete) {
                stringRedisTemplate.delete(userToken);
            }
            return value;
        }
    }

    public User generateToken(User user) {
        String token = (UUID.randomUUID().toString() + UUID.randomUUID()).replaceAll("-", "");
        String dbToken = stringRedisTemplate.opsForValue().get(String.valueOf(user.getId()));
        if (dbToken != null) {
            stringRedisTemplate.delete(String.valueOf(user.getId()));
            stringRedisTemplate.delete(dbToken);
        }
        while (true) {
            if (get(token, false, false) == null) {
                break;
            } else {
                token = (UUID.randomUUID().toString() + UUID.randomUUID()).replaceAll("-", "");
            }
        }
        add(String.valueOf(user.getId()), token);
        user.setPassword(token);
        return user;
    }
}
