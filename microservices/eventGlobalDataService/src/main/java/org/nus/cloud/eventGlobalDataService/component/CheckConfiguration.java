package org.nus.cloud.eventGlobalDataService.component;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CheckConfiguration {

    @Value("${pod-index:}")
    private String podIndex;

    public static boolean configurationValid = true;

    @PostConstruct
    public void checkConfiguration() {
        if (podIndex.isBlank()) {
            log.error("Pod index not set, the service will not work properly");
            configurationValid = false;
        }
    }
}
