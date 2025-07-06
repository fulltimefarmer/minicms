package org.max.cms.leave.config;

import org.springframework.context.annotation.Configuration;

/**
 * Camunda工作流配置
 */
@Configuration
public class CamundaConfig {
    
    // Camunda的基本配置会通过自动配置完成
    // Camunda与Spring Security的集成比Activiti更好，不需要额外排除配置
}