package org.cs304.backend.component;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CheckConfiguration {

    @Value("${file-server.host:}")
    private String fileServerHost;

    @Value("${file-server.port:}")
    private String fileServerPort;

    @Value("${file-server.admin-token:}")
    private String adminToken;

    @Value("${user-token.file-server:}")
    private String fileServerUserToken;

    @Value("${amap-security-js-code:}")
    private String amapSecurityJsCode;

    @PostConstruct
    public void checkConfiguration() {
        if (fileServerHost.isBlank()) {
            log.warn("File server host not set, some features related to file management will be disabled");
        }
        if (fileServerPort.isBlank()) {
            log.warn("File server port not set, some features related to file management will be disabled");
        }
        if (adminToken.isBlank()) {
            log.warn("Admin token of file server not set, some features related to file management will be disabled");
        }
        if (fileServerUserToken.isBlank()) {
            log.warn("Token for file server user not set, some features related to file management will be disabled");
        }
        if (amapSecurityJsCode.isBlank()) {
            log.warn("AMap security js code not set, some features related to AMap will not work properly");
        }
    }
}
