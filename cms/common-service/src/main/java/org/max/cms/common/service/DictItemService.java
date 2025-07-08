package org.max.cms.common.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.max.cms.common.entity.DictItem;
import org.max.cms.common.repository.DictItemRepository.DictItemQuery;

import java.util.List;

/**
 * 数据字典项服务接口
 */
public interface DictItemService {
    
    /**
     * 分页查询字典项
     */
    IPage<DictItem> page(Page<DictItem> page, DictItemQuery query);
    
    /**
     * 根据ID查询字典项
     */
    DictItem getById(Long id);
    
    /**
     * 根据类型ID查询所有字典项
     */
    List<DictItem> getByTypeId(Long typeId);
    
    /**
     * 根据类型编码查询所有字典项
     */
    List<DictItem> getByTypeCode(String typeCode);
    
    /**
     * 根据类型编码和字典项编码查询字典项
     */
    DictItem getByTypeCodeAndItemCode(String typeCode, String itemCode);
    
    /**
     * 查询子级字典项
     */
    List<DictItem> getChildren(Long parentId);
    
    /**
     * 保存字典项
     */
    boolean save(DictItem dictItem);
    
    /**
     * 更新字典项
     */
    boolean updateById(DictItem dictItem);
    
    /**
     * 删除字典项
     */
    boolean deleteById(Long id);
    
    /**
     * 批量删除字典项
     */
    boolean deleteBatch(List<Long> ids);
    
    /**
     * 启用/禁用字典项
     */
    boolean updateStatus(Long id, String status);
    
    /**
     * 检查字典项编码是否存在
     */
    boolean existsByItemCode(Long typeId, String itemCode, Long excludeId);
    
    /**
     * 根据字典值获取字典项名称
     */
    String getItemNameByValue(String typeCode, String itemValue);
    
    /**
     * 根据字典编码获取字典项名称
     */
    String getItemNameByCode(String typeCode, String itemCode);
}