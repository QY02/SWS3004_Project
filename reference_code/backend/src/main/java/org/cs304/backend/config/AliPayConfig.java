package org.cs304.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.stereotype.Component;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Component
@ConfigurationProperties(prefix="alipay")
public class AliPayConfig {
    private String appId;
    private String appPrivateKey;
    private String alipayPublicKey;
    private String notifyUrl;

    public String getAppId(){
        return this.appId;
    }
    public void setAppId(String appId){
        this.appId = appId;
    }

    public String getAppPrivateKey(){
        return this.appPrivateKey;
    }
    
    public void setAppPrivateKey(String appPrivateKey){
        this.appPrivateKey = appPrivateKey;
    }

    public String getAlipayPublicKey(){
        return this.alipayPublicKey;
    }
     public void setAlipayPublicKey(String alipayPublicKey){
        this.alipayPublicKey = alipayPublicKey;
    }

    public String getNotifyUrl(){
        return this.notifyUrl;
    }
     public void setNotifyUrl(String notifyUrl){
        this.notifyUrl = notifyUrl;
    }
    
}
