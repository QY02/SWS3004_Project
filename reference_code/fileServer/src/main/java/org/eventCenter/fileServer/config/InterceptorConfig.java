package org.eventCenter.fileServer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(TokenInterceptor()).addPathPatterns("/**")
                .excludePathPatterns("/swagger-ui*/**", "/v3/api-docs/**");
    }

    @Bean
    public TokenInterceptor TokenInterceptor() {
        return new TokenInterceptor();
    }
}
