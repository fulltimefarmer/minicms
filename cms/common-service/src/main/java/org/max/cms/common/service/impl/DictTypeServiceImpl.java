package org.max.cms.common.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.max.cms.common.entity.DictType;
import org.max.cms.common.repository.DictTypeRepository;
import org.max.cms.common.repository.DictTypeRepository.DictTypeQuery;
import org.max.cms.common.service.DictTypeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 数据字典类型服务实现类
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DictTypeServiceImpl implements DictTypeService {
    
    private final DictTypeRepository dictTypeRepository;
    
    @Override
    public IPage<DictType> page(Page<DictType> page, DictTypeQuery query) {
        return dictTypeRepository.selectDictTypePage(page, query);
    }
    
    @Override
    public DictType getById(Long id) {
        Assert.notNull(id, "字典类型ID不能为空");
        return dictTypeRepository.selectById(id);
    }
    
    @Override
    public DictType getByTypeCode(String typeCode) {
        Assert.hasText(typeCode, "字典类型编码不能为空");
        return dictTypeRepository.selectByTypeCode(typeCode);
    }
    
    @Override
    public List<DictType> getAllActive() {
        return dictTypeRepository.selectAllActive();
    }
    
    @Override
    @Transactional
    public boolean save(DictType dictType) {
        Assert.notNull(dictType, "字典类型不能为空");
        Assert.hasText(dictType.getTypeCode(), "字典类型编码不能为空");
        Assert.hasText(dictType.getTypeName(), "字典类型名称不能为空");
        
        // 检查类型编码是否存在
        if (existsByTypeCode(dictType.getTypeCode(), null)) {
            throw new IllegalArgumentException("字典类型编码已存在: " + dictType.getTypeCode());
        }
        
        // 设置默认值
        if (!StringUtils.hasText(dictType.getStatus())) {
            dictType.setStatus("ACTIVE");
        }
        if (dictType.getSortOrder() == null) {
            dictType.setSortOrder(0);
        }
        
        try {
            int result = dictTypeRepository.insert(dictType);
            log.info("保存字典类型成功: {}", dictType.getTypeCode());
            return result > 0;
        } catch (Exception e) {
            log.error("保存字典类型失败: {}", dictType.getTypeCode(), e);
            throw new RuntimeException("保存字典类型失败", e);
        }
    }
    
    @Override
    @Transactional
    public boolean updateById(DictType dictType) {
        Assert.notNull(dictType, "字典类型不能为空");
        Assert.notNull(dictType.getId(), "字典类型ID不能为空");
        Assert.hasText(dictType.getTypeCode(), "字典类型编码不能为空");
        Assert.hasText(dictType.getTypeName(), "字典类型名称不能为空");
        
        // 检查类型编码是否存在（排除自己）
        if (existsByTypeCode(dictType.getTypeCode(), dictType.getId())) {
            throw new IllegalArgumentException("字典类型编码已存在: " + dictType.getTypeCode());
        }
        
        try {
            int result = dictTypeRepository.updateById(dictType);
            log.info("更新字典类型成功: {}", dictType.getTypeCode());
            return result > 0;
        } catch (Exception e) {
            log.error("更新字典类型失败: {}", dictType.getTypeCode(), e);
            throw new RuntimeException("更新字典类型失败", e);
        }
    }
    
    @Override
    @Transactional
    public boolean deleteById(Long id) {
        Assert.notNull(id, "字典类型ID不能为空");
        
        DictType dictType = getById(id);
        if (dictType == null) {
            throw new IllegalArgumentException("字典类型不存在");
        }
        
        try {
            // 软删除
            dictType.setDeleted(true);
            int result = dictTypeRepository.updateById(dictType);
            log.info("删除字典类型成功: {}", dictType.getTypeCode());
            return result > 0;
        } catch (Exception e) {
            log.error("删除字典类型失败: {}", dictType.getTypeCode(), e);
            throw new RuntimeException("删除字典类型失败", e);
        }
    }
    
    @Override
    @Transactional
    public boolean deleteBatch(List<Long> ids) {
        Assert.notEmpty(ids, "删除的字典类型ID列表不能为空");
        
        try {
            int successCount = 0;
            for (Long id : ids) {
                if (deleteById(id)) {
                    successCount++;
                }
            }
            log.info("批量删除字典类型成功，总数: {}, 成功: {}", ids.size(), successCount);
            return successCount == ids.size();
        } catch (Exception e) {
            log.error("批量删除字典类型失败", e);
            throw new RuntimeException("批量删除字典类型失败", e);
        }
    }
    
    @Override
    @Transactional
    public boolean updateStatus(Long id, String status) {
        Assert.notNull(id, "字典类型ID不能为空");
        Assert.hasText(status, "状态不能为空");
        
        DictType dictType = getById(id);
        if (dictType == null) {
            throw new IllegalArgumentException("字典类型不存在");
        }
        
        try {
            dictType.setStatus(status);
            int result = dictTypeRepository.updateById(dictType);
            log.info("更新字典类型状态成功: {}, 状态: {}", dictType.getTypeCode(), status);
            return result > 0;
        } catch (Exception e) {
            log.error("更新字典类型状态失败: {}, 状态: {}", dictType.getTypeCode(), status, e);
            throw new RuntimeException("更新字典类型状态失败", e);
        }
    }
    
    @Override
    public boolean existsByTypeCode(String typeCode, Long excludeId) {
        Assert.hasText(typeCode, "字典类型编码不能为空");
        return dictTypeRepository.existsByTypeCode(typeCode, excludeId);
    }
}