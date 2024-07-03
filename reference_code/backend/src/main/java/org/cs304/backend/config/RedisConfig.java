package org.cs304.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * AI-generated-content
 * tool: GitHub Copilot
 * version: latest
 * usage: 我问Copilot如何同时连接到两个redis数据库，它给我写了这个配置文件，我修改了@Value里的参数以及Bean和方法的名字，并且修了一些bug
 */


@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${spring.data.redis.password}")
    private String password;

    @Value("${spring.data.redis.database-index-authentication}")
    private int databaseIndexAuthentication;

    @Value("${spring.data.redis.database-index-file}")
    private int databaseIndexFile;

    @Bean(name = "stringRedisTemplateAuthentication")
    public StringRedisTemplate stringRedisTemplateAuthentication() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
        config.setPassword(RedisPassword.of(password));
        config.setDatabase(databaseIndexAuthentication);
        LettuceConnectionFactory factory = new LettuceConnectionFactory(config);
        factory.afterPropertiesSet();
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(factory);
        return template;
    }

    @Bean(name = "stringRedisTemplateFile")
    public StringRedisTemplate stringRedisTemplateFile() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
        config.setPassword(RedisPassword.of(password));
        config.setDatabase(databaseIndexFile);
        LettuceConnectionFactory factory = new LettuceConnectionFactory(config);
        factory.afterPropertiesSet();
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(factory);
        return template;
    }
}
