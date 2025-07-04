package org.max.cms.common.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Auditable {
    
    /**
     * 操作动作
     */
    String action();
    
    /**
     * 资源类型
     */
    String resource();
    
    /**
     * 操作描述
     */
    String description() default "";
}