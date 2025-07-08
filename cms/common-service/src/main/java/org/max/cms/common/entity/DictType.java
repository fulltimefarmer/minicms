package org.max.cms.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 数据字典类型实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dict_type")
public class DictType extends BaseEntity {
    
    /**
     * 字典类型编码
     */
    private String typeCode;
    
    /**
     * 字典类型名称
     */
    private String typeName;
    
    /**
     * 描述
     */
    private String description;
    
    /**
     * 状态：ACTIVE-启用，INACTIVE-禁用
     */
    private String status;
    
    /**
     * 排序顺序
     */
    private Integer sortOrder;
}