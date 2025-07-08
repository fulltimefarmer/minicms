package org.max.cms.common.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.max.cms.common.entity.DictItem;

import java.util.List;

/**
 * 数据字典项数据访问层
 */
@Mapper
public interface DictItemRepository extends BaseMapper<DictItem> {
    
    /**
     * 分页查询字典项
     */
    @Select({
        "<script>",
        "SELECT di.*, dt.type_name as type_name FROM dict_item di",
        "LEFT JOIN dict_type dt ON di.type_id = dt.id",
        "WHERE di.deleted = false",
        "<if test='query.typeId != null'>",
        "  AND di.type_id = #{query.typeId}",
        "</if>",
        "<if test='query.itemCode != null and query.itemCode != \"\"'>",
        "  AND di.item_code LIKE CONCAT('%', #{query.itemCode}, '%')",
        "</if>",
        "<if test='query.itemName != null and query.itemName != \"\"'>",
        "  AND di.item_name LIKE CONCAT('%', #{query.itemName}, '%')",
        "</if>",
        "<if test='query.status != null and query.status != \"\"'>",
        "  AND di.status = #{query.status}",
        "</if>",
        "<if test='query.parentId != null'>",
        "  AND di.parent_id = #{query.parentId}",
        "</if>",
        "<if test='query.keyword != null and query.keyword != \"\"'>",
        "  AND (",
        "    di.item_code LIKE CONCAT('%', #{query.keyword}, '%')",
        "    OR di.item_name LIKE CONCAT('%', #{query.keyword}, '%')",
        "    OR di.item_value LIKE CONCAT('%', #{query.keyword}, '%')",
        "    OR di.description LIKE CONCAT('%', #{query.keyword}, '%')",
        "  )",
        "</if>",
        "ORDER BY di.sort_order ASC, di.created_at DESC",
        "</script>"
    })
    IPage<DictItem> selectDictItemPage(Page<DictItem> page, @Param("query") DictItemQuery query);
    
    /**
     * 根据类型ID查询所有字典项
     */
    @Select({
        "SELECT * FROM dict_item",
        "WHERE type_id = #{typeId}",
        "  AND deleted = false",
        "ORDER BY sort_order ASC, created_at DESC"
    })
    List<DictItem> selectByTypeId(@Param("typeId") Long typeId);
    
    /**
     * 根据类型编码查询所有字典项
     */
    @Select({
        "SELECT di.* FROM dict_item di",
        "INNER JOIN dict_type dt ON di.type_id = dt.id",
        "WHERE dt.type_code = #{typeCode}",
        "  AND di.status = 'ACTIVE'",
        "  AND dt.status = 'ACTIVE'",
        "  AND di.deleted = false",
        "  AND dt.deleted = false",
        "ORDER BY di.sort_order ASC, di.created_at DESC"
    })
    List<DictItem> selectByTypeCode(@Param("typeCode") String typeCode);
    
    /**
     * 根据类型编码和字典项编码查询字典项
     */
    @Select({
        "SELECT di.* FROM dict_item di",
        "INNER JOIN dict_type dt ON di.type_id = dt.id",
        "WHERE dt.type_code = #{typeCode}",
        "  AND di.item_code = #{itemCode}",
        "  AND di.deleted = false",
        "  AND dt.deleted = false"
    })
    DictItem selectByTypeCodeAndItemCode(@Param("typeCode") String typeCode, @Param("itemCode") String itemCode);
    
    /**
     * 查询子级字典项
     */
    @Select({
        "SELECT * FROM dict_item",
        "WHERE parent_id = #{parentId}",
        "  AND deleted = false",
        "ORDER BY sort_order ASC, created_at DESC"
    })
    List<DictItem> selectChildren(@Param("parentId") Long parentId);
    
    /**
     * 检查字典项编码是否存在
     */
    @Select({
        "SELECT COUNT(*) > 0 FROM dict_item",
        "WHERE type_id = #{typeId}",
        "  AND item_code = #{itemCode}",
        "  AND deleted = false",
        "<if test='excludeId != null'>",
        "  AND id != #{excludeId}",
        "</if>"
    })
    boolean existsByItemCode(@Param("typeId") Long typeId, @Param("itemCode") String itemCode, @Param("excludeId") Long excludeId);
    
    /**
     * 字典项查询条件类
     */
    class DictItemQuery {
        private Long typeId;
        private String itemCode;
        private String itemName;
        private String status;
        private Long parentId;
        private String keyword;
        
        // Getters and Setters
        public Long getTypeId() { return typeId; }
        public void setTypeId(Long typeId) { this.typeId = typeId; }
        
        public String getItemCode() { return itemCode; }
        public void setItemCode(String itemCode) { this.itemCode = itemCode; }
        
        public String getItemName() { return itemName; }
        public void setItemName(String itemName) { this.itemName = itemName; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public Long getParentId() { return parentId; }
        public void setParentId(Long parentId) { this.parentId = parentId; }
        
        public String getKeyword() { return keyword; }
        public void setKeyword(String keyword) { this.keyword = keyword; }
    }
}