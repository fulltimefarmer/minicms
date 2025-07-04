package org.max.cms.common.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan(basePackages = "org.max.cms.common")
@EntityScan(basePackages = "org.max.cms.common.entity")
@EnableJpaRepositories(basePackages = "org.max.cms.common.repository")
public class CommonConfig {
}