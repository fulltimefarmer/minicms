package org.max.cms.asset.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@Slf4j
public class FileStorageConfig {

    @Autowired
    private DocumentConfig documentConfig;

    @PostConstruct
    public void init() {
        try {
            Path uploadPath = Paths.get(documentConfig.getStoragePath());
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                log.info("Created file storage directory: {}", uploadPath.toAbsolutePath());
            } else {
                log.info("File storage directory already exists: {}", uploadPath.toAbsolutePath());
            }
        } catch (IOException e) {
            log.error("Failed to create file storage directory", e);
        }
    }
}