package org.max.cms.common.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.max.cms.common.entity.DictType;

import java.util.List;

/**
 * 数据字典类型数据访问层
 */
@Mapper
public interface DictTypeRepository extends BaseMapper<DictType> {
    
    /**
     * 分页查询字典类型
     */
    @Select({
        "<script>",
        "SELECT * FROM dict_type",
        "WHERE deleted = false",
        "<if test='query.typeCode != null and query.typeCode != \"\"'>",
        "  AND type_code LIKE CONCAT('%', #{query.typeCode}, '%')",
        "</if>",
        "<if test='query.typeName != null and query.typeName != \"\"'>",
        "  AND type_name LIKE CONCAT('%', #{query.typeName}, '%')",
        "</if>",
        "<if test='query.status != null and query.status != \"\"'>",
        "  AND status = #{query.status}",
        "</if>",
        "<if test='query.keyword != null and query.keyword != \"\"'>",
        "  AND (",
        "    type_code LIKE CONCAT('%', #{query.keyword}, '%')",
        "    OR type_name LIKE CONCAT('%', #{query.keyword}, '%')",
        "    OR description LIKE CONCAT('%', #{query.keyword}, '%')",
        "  )",
        "</if>",
        "ORDER BY sort_order ASC, created_at DESC",
        "</script>"
    })
    IPage<DictType> selectDictTypePage(Page<DictType> page, @Param("query") DictTypeQuery query);
    
    /**
     * 根据类型编码查询字典类型
     */
    @Select({
        "SELECT * FROM dict_type",
        "WHERE type_code = #{typeCode}",
        "  AND deleted = false"
    })
    DictType selectByTypeCode(@Param("typeCode") String typeCode);
    
    /**
     * 查询所有启用的字典类型
     */
    @Select({
        "SELECT * FROM dict_type",
        "WHERE status = 'ACTIVE'",
        "  AND deleted = false",
        "ORDER BY sort_order ASC, created_at DESC"
    })
    List<DictType> selectAllActive();
    
    /**
     * 检查类型编码是否存在
     */
    @Select({
        "SELECT COUNT(*) > 0 FROM dict_type",
        "WHERE type_code = #{typeCode}",
        "  AND deleted = false",
        "<if test='excludeId != null'>",
        "  AND id != #{excludeId}",
        "</if>"
    })
    boolean existsByTypeCode(@Param("typeCode") String typeCode, @Param("excludeId") Long excludeId);
    
    /**
     * 字典类型查询条件类
     */
    class DictTypeQuery {
        private String typeCode;
        private String typeName;
        private String status;
        private String keyword;
        
        // Getters and Setters
        public String getTypeCode() { return typeCode; }
        public void setTypeCode(String typeCode) { this.typeCode = typeCode; }
        
        public String getTypeName() { return typeName; }
        public void setTypeName(String typeName) { this.typeName = typeName; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public String getKeyword() { return keyword; }
        public void setKeyword(String keyword) { this.keyword = keyword; }
    }
}