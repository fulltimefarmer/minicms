package org.max.cms.common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 数据字典项实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dict_item")
public class DictItem extends BaseEntity {
    
    /**
     * 字典类型ID
     */
    private Long typeId;
    
    /**
     * 字典项编码
     */
    private String itemCode;
    
    /**
     * 字典项名称
     */
    private String itemName;
    
    /**
     * 字典项值
     */
    private String itemValue;
    
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
    
    /**
     * 父级字典项ID（支持层级结构）
     */
    private Long parentId;
    
    /**
     * 层级深度
     */
    private Integer levelDepth;
    
    /**
     * CSS样式类
     */
    private String cssClass;
    
    /**
     * 图标
     */
    private String icon;
}