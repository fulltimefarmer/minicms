package org.max.cms.auth.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.util.List;

/**
 * 安全路径配置类
 * 统一管理不需要JWT验证的URL路径
 */
@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "auth.security")
public class SecurityPathsConfig {
    
    /**
     * 不需要JWT验证的URL路径列表
     */
    private List<String> excludePaths;
    
    @PostConstruct
    public void init() {
        log.info("Security exclude paths configured: {}", excludePaths);
    }
    
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
            log.debug("Exclude paths or path is null - excludePaths: {}, path: {}", excludePaths, path);
            return false;
        }
        
        log.debug("Checking path '{}' against exclude paths: {}", path, excludePaths);
        
        boolean result = excludePaths.stream().anyMatch(excludePath -> {
            // 处理通配符匹配
            if (excludePath.endsWith("/**")) {
                String prefix = excludePath.substring(0, excludePath.length() - 3);
                boolean matches = path.startsWith(prefix);
                log.debug("Wildcard /** match: {} vs {} -> {}", path, excludePath, matches);
                return matches;
            } else if (excludePath.endsWith("/*")) {
                String prefix = excludePath.substring(0, excludePath.length() - 2);
                boolean matches = path.startsWith(prefix) && !path.substring(prefix.length()).contains("/");
                log.debug("Wildcard /* match: {} vs {} -> {}", path, excludePath, matches);
                return matches;
            } else {
                boolean matches = path.equals(excludePath);
                log.debug("Exact match: {} vs {} -> {}", path, excludePath, matches);
                return matches;
            }
        });
        
        log.debug("Final result for path '{}': {}", path, result);
        return result;
    }
}