package org.max.cms.asset;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class AssetServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(AssetServiceApplication.class, args);
    }
}