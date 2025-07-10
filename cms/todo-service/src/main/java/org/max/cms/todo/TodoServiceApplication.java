package org.max.cms.todo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 待办事项服务启动类
 */
@SpringBootApplication(scanBasePackages = {"org.max.cms.todo", "org.max.cms.common"})
@EntityScan(basePackages = {"org.max.cms.todo.entity", "org.max.cms.common.entity"})
@EnableJpaRepositories(basePackages = "org.max.cms.todo.mapper")
@EnableTransactionManagement
public class TodoServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TodoServiceApplication.class, args);
    }
}