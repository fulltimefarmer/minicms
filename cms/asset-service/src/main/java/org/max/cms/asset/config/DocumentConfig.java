package org.max.cms.asset.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "document")
@Data
public class DocumentConfig {
    
    /**
     * 文件存储路径
     */
    private String storagePath = "files";
    
    /**
     * 允许的文件类型
     */
    private String[] allowedTypes = {
        "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", 
        "txt", "jpg", "jpeg", "png", "gif", "bmp", "zip", "rar"
    };
    
    /**
     * 最大文件大小（字节）
     */
    private long maxFileSize = 10 * 1024 * 1024; // 10MB
}