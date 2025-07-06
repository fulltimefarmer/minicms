package org.max.cms.leave;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "org.max.cms")
public class LeaveServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LeaveServiceApplication.class, args);
    }
}