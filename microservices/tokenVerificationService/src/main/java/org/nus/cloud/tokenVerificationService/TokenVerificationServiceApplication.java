package org.nus.cloud.tokenVerificationService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;

@SpringBootApplication
public class TokenVerificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(TokenVerificationServiceApplication.class);
        springApplication.addListeners(new ApplicationPidFileWriter("app.pid"));
        springApplication.run(args);
    }

}
