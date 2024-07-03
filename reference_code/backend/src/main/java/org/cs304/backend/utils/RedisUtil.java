package org.cs304.backend.utils;

import jakarta.annotation.Resource;
import org.cs304.backend.entity.User;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {

    @Resource(name = "stringRedisTemplateAuthentication")
    private StringRedisTemplate stringRedisTemplateAuthentication;
    @Resource(name = "stringRedisTemplateFile")
    private StringRedisTemplate stringRedisTemplateFile;
    private final long timeoutDefault = 3600;
    private final TimeUnit timeUnit = TimeUnit.SECONDS;
    private final Object authenticationTokenLock = new Object();
    private final Object fileTokenLock = new Object();

    public void add(String value, String key) {
        if ((value != null) && (key != null)) {
            stringRedisTemplateAuthentication.opsForValue().set(key, value, timeoutDefault, timeUnit);
            stringRedisTemplateAuthentication.opsForValue().set(value, key, timeoutDefault, timeUnit);
        }
    }

    public void add(String key, String value, long timeout) {
        if ((key != null) && (value != null)) {
            stringRedisTemplateAuthentication.opsForValue().set(key, value, timeout, timeUnit);
        }
    }

    public void add(String key, long timeout) {
        if (key != null) {
            stringRedisTemplateAuthentication.opsForValue().set(key, "", timeout, timeUnit);
        }
    }

    public String get(String userToken, boolean resetExpireTime, boolean delete) {
        if (userToken == null) {
            return null;
        } else {
//            if (resetExpireTime) {
//                stringRedisTemplate.expire(userToken, timeout, timeUnit);
//            }
            String value = stringRedisTemplateAuthentication.opsForValue().get(userToken);
            if (value == null) {
                return null;
            }
            if (delete) {
                stringRedisTemplateAuthentication.delete(userToken);
            }
            return value;
        }
    }

    public String getInterceptor(String userToken, boolean resetExpireTime, boolean delete) {
        if (userToken == null) {
            return null;
        } else {
            String value;
            if (resetExpireTime) {
                stringRedisTemplateAuthentication.expire(userToken, timeoutDefault, timeUnit);
                value = stringRedisTemplateAuthentication.opsForValue().get(userToken);
                if (value != null) {
                    stringRedisTemplateAuthentication.opsForValue().set(value, userToken, timeoutDefault, timeUnit);
                }
            }
            else {
                value = stringRedisTemplateAuthentication.opsForValue().get(userToken);
            }
            if (value == null) {
                return null;
            }
            if (delete) {
                stringRedisTemplateAuthentication.delete(userToken);
            }
            return value;
        }
    }

    public User generateToken(User user) {
        String token = (UUID.randomUUID().toString() + UUID.randomUUID()).replaceAll("-", "");
        String dbToken = stringRedisTemplateAuthentication.opsForValue().get(user.getId());
        if (dbToken != null) {
            stringRedisTemplateAuthentication.delete(user.getId());
            stringRedisTemplateAuthentication.delete(dbToken);
        }
        synchronized (authenticationTokenLock) {
            while (true) {
                if (get(token, false, false) == null) {
                    break;
                } else {
                    token = (UUID.randomUUID().toString() + UUID.randomUUID()).replaceAll("-", "");
                }
            }
            add(user.getId(), token);
        }
        user.setPassword(token);
        return user;
    }

    public String generateAndAddFileToken(String filePath) {
        String fileToken = UUID.randomUUID().toString().replaceAll("-", "");
        synchronized (fileTokenLock) {
            while (true) {
                if (get(fileToken, false, false) == null) {
                    break;
                }
                else {
                    fileToken = UUID.randomUUID().toString().replaceAll("-", "");
                }
            }
            stringRedisTemplateFile.opsForValue().set(fileToken, filePath, timeoutDefault, timeUnit);
        }
        return fileToken;
    }

    public void addFileToken(String fileToken, String filePath) {
        stringRedisTemplateFile.opsForValue().set(fileToken, filePath, timeoutDefault, timeUnit);
    }
}
