package org.nus.cloud.eventPublishService.component;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CheckConfiguration {

    @Value("${event-global-data-service.host:}")
    private String eventGlobalDataServiceHost;

    @Value("${event-global-data-service.port:}")
    private String eventGlobalDataServicePort;

    public static boolean configurationValid = true;

    @PostConstruct
    public void checkConfiguration() {
        if (eventGlobalDataServiceHost.isBlank()) {
            log.error("Event global data service host not set, the service will not work properly");
            configurationValid = false;
        }
        if (eventGlobalDataServicePort.isBlank()) {
            log.error("Event global data service port not set, the service will not work properly");
            configurationValid = false;
        }
    }
}
