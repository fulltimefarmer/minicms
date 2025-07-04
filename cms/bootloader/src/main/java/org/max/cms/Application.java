package org.max.cms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {
    "org.max.cms.common",
    "org.max.cms.auth", 
    "org.max.cms.user",
    "org.max.cms.asset"
})
@EntityScan(basePackages = {
    "org.max.cms.common.entity",
    "org.max.cms.auth.entity",
    "org.max.cms.user.entity", 
    "org.max.cms.asset.entity"
})
@EnableJpaRepositories(basePackages = {
    "org.max.cms.common.repository",
    "org.max.cms.auth.repository",
    "org.max.cms.user.repository",
    "org.max.cms.asset.repository"
})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}