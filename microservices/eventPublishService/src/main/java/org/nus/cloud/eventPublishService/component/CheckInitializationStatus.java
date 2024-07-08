package org.nus.cloud.eventPublishService.component;

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
        if (CheckConfiguration.configurationValid) {
            String address = serverProperties.getAddress() != null ? serverProperties.getAddress().getHostAddress() : "localhost";
            int port = serverProperties.getPort() != null ? serverProperties.getPort() : 8080;
            log.info("EventPublishService is running at http://" + address + ":" + port);
        } else {
            log.error("There are some errors in initialization, the service will be stopped");
            applicationContext.close();
        }
    }
}
