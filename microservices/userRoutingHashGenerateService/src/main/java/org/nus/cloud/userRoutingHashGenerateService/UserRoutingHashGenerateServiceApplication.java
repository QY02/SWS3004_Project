package org.nus.cloud.userRoutingHashGenerateService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;

@SpringBootApplication
public class UserRoutingHashGenerateServiceApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(UserRoutingHashGenerateServiceApplication.class);
        springApplication.addListeners(new ApplicationPidFileWriter("app.pid"));
        springApplication.run(args);
    }

}
