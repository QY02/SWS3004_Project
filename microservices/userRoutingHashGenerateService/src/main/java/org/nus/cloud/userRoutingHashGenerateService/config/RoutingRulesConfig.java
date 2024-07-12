package org.nus.cloud.userRoutingHashGenerateService.config;

import lombok.Data;
import org.nus.cloud.userRoutingHashGenerateService.entity.Rule;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "routing-rules")
public class RoutingRulesConfig {
    private List<Rule> ruleList;
}
