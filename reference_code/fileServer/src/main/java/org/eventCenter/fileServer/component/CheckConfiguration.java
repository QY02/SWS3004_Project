package org.eventCenter.fileServer.component;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CheckConfiguration {

    @Value("${admin-token:}")
    private String adminToken;

    @Value("${backend.host:}")
    private String backendHost;

    @Value("${backend.port:}")
    private String backendPort;

    @Value("${backend.api-token:}")
    private String backendApiToken;

    @PostConstruct
    public void checkConfiguration() {
        if (adminToken.isBlank()) {
            log.error("Admin token not set, the server will reject all the requests");
        }
        if (backendHost.isBlank()) {
            log.warn("Backend host not set, some features will be disabled");
        }
        if (backendPort.isBlank()) {
            log.warn("Backend port not set, some features will be disabled");
        }
        if (backendApiToken.isBlank()) {
            log.warn("Backend api token not set, some features will be disabled");
        }
    }
}
