package org.max.cms.common.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.max.cms.common.entity.DictType;
import org.max.cms.common.repository.DictTypeRepository.DictTypeQuery;

import java.util.List;

/**
 * 数据字典类型服务接口
 */
public interface DictTypeService {
    
    /**
     * 分页查询字典类型
     */
    IPage<DictType> page(Page<DictType> page, DictTypeQuery query);
    
    /**
     * 根据ID查询字典类型
     */
    DictType getById(Long id);
    
    /**
     * 根据类型编码查询字典类型
     */
    DictType getByTypeCode(String typeCode);
    
    /**
     * 查询所有启用的字典类型
     */
    List<DictType> getAllActive();
    
    /**
     * 保存字典类型
     */
    boolean save(DictType dictType);
    
    /**
     * 更新字典类型
     */
    boolean updateById(DictType dictType);
    
    /**
     * 删除字典类型
     */
    boolean deleteById(Long id);
    
    /**
     * 批量删除字典类型
     */
    boolean deleteBatch(List<Long> ids);
    
    /**
     * 启用/禁用字典类型
     */
    boolean updateStatus(Long id, String status);
    
    /**
     * 检查类型编码是否存在
     */
    boolean existsByTypeCode(String typeCode, Long excludeId);
}