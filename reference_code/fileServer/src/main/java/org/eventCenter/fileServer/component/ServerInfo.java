package org.eventCenter.fileServer.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ServerInfo {

    private final ServerProperties serverProperties;

    public ServerInfo(ServerProperties serverProperties) {
        this.serverProperties = serverProperties;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void printServerInfo() {
        String address = serverProperties.getAddress() != null ? serverProperties.getAddress().getHostAddress() : "localhost";
        int port = serverProperties.getPort() != null ? serverProperties.getPort() : 8080;
        log.info("FileServer is running at http://" + address + ":" + port);
        log.info("FileServer Swagger is running at http://" + address + ":" + port + "/swagger-ui/index.html");
    }
}
