package org.nus.cloud.userRoutingHashGenerateService.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CheckInitializationStatus {

    private final ServerProperties serverProperties;

    private final ConfigurableApplicationContext applicationContext;

    public CheckInitializationStatus(ServerProperties serverProperties, ConfigurableApplicationContext applicationContext) {
        this.serverProperties = serverProperties;
        this.applicationContext = applicationContext;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void checkInitializationStatus() {
        if (applicationContext.getEnvironment().containsProperty("routingRuleList[0].startHash")) {
            String address = serverProperties.getAddress() != null ? serverProperties.getAddress().getHostAddress() : "localhost";
            int port = serverProperties.getPort() != null ? serverProperties.getPort() : 8080;
            log.info("UserRoutingHashGenerateService is running at http://" + address + ":" + port);
        } else {
            log.error("Routing rules not set, the service will be stopped");
            applicationContext.close();
        }
    }
}
