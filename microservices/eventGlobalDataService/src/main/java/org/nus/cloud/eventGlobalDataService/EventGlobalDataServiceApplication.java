package org.nus.cloud.eventGlobalDataService;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;

@SpringBootApplication
@MapperScan("org.nus.cloud.eventGlobalDataService.mapper")
public class EventGlobalDataServiceApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(EventGlobalDataServiceApplication.class);
        springApplication.addListeners(new ApplicationPidFileWriter("app.pid"));
        springApplication.run(args);
    }

}
