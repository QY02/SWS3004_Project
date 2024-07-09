package org.nus.cloud.eventDetailedDataService;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;

@SpringBootApplication
@MapperScan("org.nus.cloud.eventDetailedDataService.mapper")
public class EventDetailedDataServiceApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(EventDetailedDataServiceApplication.class);
        springApplication.addListeners(new ApplicationPidFileWriter("app.pid"));
        springApplication.run(args);
    }

}
