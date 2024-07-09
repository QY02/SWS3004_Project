package org.nus.cloud.bookingService.component;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CheckConfiguration {

    @Value("${order-record-service.host:}")
    private String orderRecordServiceHost;

    @Value("${order-record-service.port:}")
    private String orderRecordServicePort;

    public static boolean configurationValid = true;

    @PostConstruct
    public void checkConfiguration() {
        if (orderRecordServiceHost.isBlank()) {
            log.error("Order record service host not set, the service will not work properly");
            configurationValid = false;
        }
        if (orderRecordServicePort.isBlank()) {
            log.error("Order record service port not set, the service will not work properly");
            configurationValid = false;
        }
    }
}
