package org.cs304.backend;

import lombok.extern.slf4j.Slf4j;
import org.cs304.backend.component.WebSocketLLMServer;
import org.cs304.backend.component.WebSocketServer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@Slf4j
@MapperScan("org.cs304.backend.mapper")
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(BackendApplication.class);
        springApplication.addListeners(new ApplicationPidFileWriter("app.pid"));
        ConfigurableApplicationContext configurableApplicationContext = springApplication.run(args);
        WebSocketLLMServer.setApplicationContext(configurableApplicationContext);
        WebSocketServer.setApplicationContext(configurableApplicationContext);
    }

}
