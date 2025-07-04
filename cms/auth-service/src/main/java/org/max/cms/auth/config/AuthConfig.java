package org.max.cms.auth.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan(basePackages = "org.max.cms.auth")
@EntityScan(basePackages = "org.max.cms.auth.entity")
@EnableJpaRepositories(basePackages = "org.max.cms.auth.repository")
public class AuthConfig {
}