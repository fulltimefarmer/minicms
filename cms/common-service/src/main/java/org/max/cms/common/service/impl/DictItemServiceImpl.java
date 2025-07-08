package org.max.cms.common.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.max.cms.common.entity.DictItem;
import org.max.cms.common.repository.DictItemRepository;
import org.max.cms.common.repository.DictItemRepository.DictItemQuery;
import org.max.cms.common.service.DictItemService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 数据字典项服务实现类
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DictItemServiceImpl implements DictItemService {
    
    private final DictItemRepository dictItemRepository;
    
    @Override
    public IPage<DictItem> page(Page<DictItem> page, DictItemQuery query) {
        return dictItemRepository.selectDictItemPage(page, query);
    }
    
    @Override
    public DictItem getById(Long id) {
        Assert.notNull(id, "字典项ID不能为空");
        return dictItemRepository.selectById(id);
    }
    
    @Override
    public List<DictItem> getByTypeId(Long typeId) {
        Assert.notNull(typeId, "字典类型ID不能为空");
        return dictItemRepository.selectByTypeId(typeId);
    }
    
    @Override
    public List<DictItem> getByTypeCode(String typeCode) {
        Assert.hasText(typeCode, "字典类型编码不能为空");
        return dictItemRepository.selectByTypeCode(typeCode);
    }
    
    @Override
    public DictItem getByTypeCodeAndItemCode(String typeCode, String itemCode) {
        Assert.hasText(typeCode, "字典类型编码不能为空");
        Assert.hasText(itemCode, "字典项编码不能为空");
        return dictItemRepository.selectByTypeCodeAndItemCode(typeCode, itemCode);
    }
    
    @Override
    public List<DictItem> getChildren(Long parentId) {
        Assert.notNull(parentId, "父级字典项ID不能为空");
        return dictItemRepository.selectChildren(parentId);
    }
    
    @Override
    @Transactional
    public boolean save(DictItem dictItem) {
        Assert.notNull(dictItem, "字典项不能为空");
        Assert.notNull(dictItem.getTypeId(), "字典类型ID不能为空");
        Assert.hasText(dictItem.getItemCode(), "字典项编码不能为空");
        Assert.hasText(dictItem.getItemName(), "字典项名称不能为空");
        
        // 检查字典项编码是否存在
        if (existsByItemCode(dictItem.getTypeId(), dictItem.getItemCode(), null)) {
            throw new IllegalArgumentException("字典项编码已存在: " + dictItem.getItemCode());
        }
        
        // 设置默认值
        if (!StringUtils.hasText(dictItem.getStatus())) {
            dictItem.setStatus("ACTIVE");
        }
        if (dictItem.getSortOrder() == null) {
            dictItem.setSortOrder(0);
        }
        if (dictItem.getLevelDepth() == null) {
            dictItem.setLevelDepth(1);
        }
        
        try {
            int result = dictItemRepository.insert(dictItem);
            log.info("保存字典项成功: {}", dictItem.getItemCode());
            return result > 0;
        } catch (Exception e) {
            log.error("保存字典项失败: {}", dictItem.getItemCode(), e);
            throw new RuntimeException("保存字典项失败", e);
        }
    }
    
    @Override
    @Transactional
    public boolean updateById(DictItem dictItem) {
        Assert.notNull(dictItem, "字典项不能为空");
        Assert.notNull(dictItem.getId(), "字典项ID不能为空");
        Assert.notNull(dictItem.getTypeId(), "字典类型ID不能为空");
        Assert.hasText(dictItem.getItemCode(), "字典项编码不能为空");
        Assert.hasText(dictItem.getItemName(), "字典项名称不能为空");
        
        // 检查字典项编码是否存在（排除自己）
        if (existsByItemCode(dictItem.getTypeId(), dictItem.getItemCode(), dictItem.getId())) {
            throw new IllegalArgumentException("字典项编码已存在: " + dictItem.getItemCode());
        }
        
        try {
            int result = dictItemRepository.updateById(dictItem);
            log.info("更新字典项成功: {}", dictItem.getItemCode());
            return result > 0;
        } catch (Exception e) {
            log.error("更新字典项失败: {}", dictItem.getItemCode(), e);
            throw new RuntimeException("更新字典项失败", e);
        }
    }
    
    @Override
    @Transactional
    public boolean deleteById(Long id) {
        Assert.notNull(id, "字典项ID不能为空");
        
        DictItem dictItem = getById(id);
        if (dictItem == null) {
            throw new IllegalArgumentException("字典项不存在");
        }
        
        try {
            // 软删除
            dictItem.setDeleted(true);
            int result = dictItemRepository.updateById(dictItem);
            log.info("删除字典项成功: {}", dictItem.getItemCode());
            return result > 0;
        } catch (Exception e) {
            log.error("删除字典项失败: {}", dictItem.getItemCode(), e);
            throw new RuntimeException("删除字典项失败", e);
        }
    }
    
    @Override
    @Transactional
    public boolean deleteBatch(List<Long> ids) {
        Assert.notEmpty(ids, "删除的字典项ID列表不能为空");
        
        try {
            int successCount = 0;
            for (Long id : ids) {
                if (deleteById(id)) {
                    successCount++;
                }
            }
            log.info("批量删除字典项成功，总数: {}, 成功: {}", ids.size(), successCount);
            return successCount == ids.size();
        } catch (Exception e) {
            log.error("批量删除字典项失败", e);
            throw new RuntimeException("批量删除字典项失败", e);
        }
    }
    
    @Override
    @Transactional
    public boolean updateStatus(Long id, String status) {
        Assert.notNull(id, "字典项ID不能为空");
        Assert.hasText(status, "状态不能为空");
        
        DictItem dictItem = getById(id);
        if (dictItem == null) {
            throw new IllegalArgumentException("字典项不存在");
        }
        
        try {
            dictItem.setStatus(status);
            int result = dictItemRepository.updateById(dictItem);
            log.info("更新字典项状态成功: {}, 状态: {}", dictItem.getItemCode(), status);
            return result > 0;
        } catch (Exception e) {
            log.error("更新字典项状态失败: {}, 状态: {}", dictItem.getItemCode(), status, e);
            throw new RuntimeException("更新字典项状态失败", e);
        }
    }
    
    @Override
    public boolean existsByItemCode(Long typeId, String itemCode, Long excludeId) {
        Assert.notNull(typeId, "字典类型ID不能为空");
        Assert.hasText(itemCode, "字典项编码不能为空");
        return dictItemRepository.existsByItemCode(typeId, itemCode, excludeId);
    }
    
    @Override
    public String getItemNameByValue(String typeCode, String itemValue) {
        if (!StringUtils.hasText(typeCode) || !StringUtils.hasText(itemValue)) {
            return null;
        }
        
        List<DictItem> items = getByTypeCode(typeCode);
        for (DictItem item : items) {
            if (itemValue.equals(item.getItemValue())) {
                return item.getItemName();
            }
        }
        return null;
    }
    
    @Override
    public String getItemNameByCode(String typeCode, String itemCode) {
        if (!StringUtils.hasText(typeCode) || !StringUtils.hasText(itemCode)) {
            return null;
        }
        
        DictItem item = getByTypeCodeAndItemCode(typeCode, itemCode);
        return item != null ? item.getItemName() : null;
    }
}