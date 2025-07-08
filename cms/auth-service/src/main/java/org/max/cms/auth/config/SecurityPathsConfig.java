package org.max.cms.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 安全路径配置类
 * 统一管理不需要JWT验证的URL路径
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "auth.security")
public class SecurityPathsConfig {
    
    /**
     * 不需要JWT验证的URL路径列表
     */
    private List<String> excludePaths;
    
    /**
     * 获取不需要验证的路径数组（用于Spring Security配置）
     */
    public String[] getExcludePathsArray() {
        return excludePaths.toArray(new String[0]);
    }
    
    /**
     * 检查指定路径是否应该被排除验证
     */
    public boolean shouldExclude(String path) {
        if (excludePaths == null || path == null) {
            return false;
        }
        
        return excludePaths.stream().anyMatch(excludePath -> {
            // 处理通配符匹配
            if (excludePath.endsWith("/**")) {
                String prefix = excludePath.substring(0, excludePath.length() - 3);
                return path.startsWith(prefix);
            } else if (excludePath.endsWith("/*")) {
                String prefix = excludePath.substring(0, excludePath.length() - 2);
                return path.startsWith(prefix) && !path.substring(prefix.length()).contains("/");
            } else {
                return path.equals(excludePath) || path.startsWith(excludePath);
            }
        });
    }
}