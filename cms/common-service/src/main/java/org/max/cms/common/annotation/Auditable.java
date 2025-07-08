package org.max.cms.common.annotation;

import java.lang.annotation.*;

/**
 * 审计注解
 * 用于标记需要进行审计记录的方法
 * 
 * 使用示例:
 * @Auditable(
 *     operationType = "CREATE",
 *     operationName = "创建用户",
 *     businessModule = "USER",
 *     riskLevel = "MEDIUM"
 * )
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Auditable {
    
    /**
     * 操作类型
     * 如: CREATE, UPDATE, DELETE, LOGIN, LOGOUT, QUERY等
     * 如果不指定，将根据HTTP方法自动推断
     */
    String operationType() default "";
    
    /**
     * 操作名称
     * 如: "创建用户", "修改密码", "删除资产"等
     */
    String operationName() default "";
    
    /**
     * 操作描述
     * 详细描述当前操作的具体内容
     */
    String operationDesc() default "";
    
    /**
     * 业务模块
     * 如: USER, ASSET, AUTH, ROLE, PERMISSION等
     */
    String businessModule() default "";
    
    /**
     * 目标对象类型
     * 如: User, Asset, Role等
     * 如果不指定，将尝试从方法参数或返回值中推断
     */
    String targetType() default "";
    
    /**
     * 风险级别
     * LOW: 低风险操作，如查询
     * MEDIUM: 中等风险操作，如创建、修改
     * HIGH: 高风险操作，如删除、权限变更
     * CRITICAL: 关键操作，如系统配置修改、批量删除
     */
    String riskLevel() default "LOW";
    
    /**
     * 是否记录请求参数
     * 默认为true，对于敏感操作可设置为false
     */
    boolean includeParams() default true;
    
    /**
     * 是否记录请求体
     * 默认为true，对于敏感数据可设置为false
     */
    boolean includeRequestBody() default true;
    
    /**
     * 是否记录响应结果
     * 默认为false，避免记录过多数据
     */
    boolean includeResponse() default false;
    
    /**
     * 是否记录请求头
     * 默认为false，避免记录敏感信息
     */
    boolean includeHeaders() default false;
    
    /**
     * 敏感字段列表
     * 在记录参数时将这些字段进行脱敏处理
     * 如: {"password", "token", "secret"}
     */
    String[] sensitiveFields() default {"password", "token", "secret", "key"};
    
    /**
     * 是否异步记录
     * 默认为true，异步记录可以提高性能
     * 对于关键操作可设置为false确保记录成功
     */
    boolean async() default true;
    
    /**
     * 自定义操作描述的SpEL表达式
     * 可以动态生成操作描述
     * 如: "删除用户: #{#username}"
     */
    String descExpression() default "";
    
    /**
     * 目标ID的SpEL表达式
     * 用于从方法参数中提取目标对象ID
     * 如: "#{#id}", "#{#user.id}"
     */
    String targetIdExpression() default "";
    
    /**
     * 目标名称的SpEL表达式
     * 用于从方法参数中提取目标对象名称
     * 如: "#{#user.username}", "#{#asset.name}"
     */
    String targetNameExpression() default "";
}