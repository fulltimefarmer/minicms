package org.max.cms.common.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.max.cms.common.entity.DictItem;
import org.max.cms.common.repository.DictItemRepository.DictItemQuery;
import org.max.cms.common.service.DictItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据字典项控制器
 */
@RestController
@RequestMapping("/api/dict-items")
@RequiredArgsConstructor
@Validated
public class DictItemController {
    
    private final DictItemService dictItemService;
    
    /**
     * 分页查询字典项
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long typeId,
            @RequestParam(required = false) String itemCode,
            @RequestParam(required = false) String itemName,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long parentId,
            @RequestParam(required = false) String keyword) {
        
        Page<DictItem> pageRequest = new Page<>(page, size);
        DictItemQuery query = new DictItemQuery();
        query.setTypeId(typeId);
        query.setItemCode(itemCode);
        query.setItemName(itemName);
        query.setStatus(status);
        query.setParentId(parentId);
        query.setKeyword(keyword);
        
        IPage<DictItem> result = dictItemService.page(pageRequest, query);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", result.getRecords());
        response.put("total", result.getTotal());
        response.put("page", result.getCurrent());
        response.put("size", result.getSize());
        response.put("pages", result.getPages());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 根据类型ID查询所有字典项
     */
    @GetMapping("/type/{typeId}")
    public ResponseEntity<Map<String, Object>> getByTypeId(@PathVariable @NotNull Long typeId) {
        List<DictItem> dictItems = dictItemService.getByTypeId(typeId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", dictItems);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 根据类型编码查询所有字典项
     */
    @GetMapping("/type-code/{typeCode}")
    public ResponseEntity<Map<String, Object>> getByTypeCode(@PathVariable String typeCode) {
        List<DictItem> dictItems = dictItemService.getByTypeCode(typeCode);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", dictItems);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 根据ID查询字典项
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable @NotNull Long id) {
        DictItem dictItem = dictItemService.getById(id);
        
        Map<String, Object> response = new HashMap<>();
        if (dictItem != null) {
            response.put("success", true);
            response.put("data", dictItem);
        } else {
            response.put("success", false);
            response.put("message", "字典项不存在");
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 根据类型编码和字典项编码查询字典项
     */
    @GetMapping("/{typeCode}/{itemCode}")
    public ResponseEntity<Map<String, Object>> getByTypeCodeAndItemCode(
            @PathVariable String typeCode,
            @PathVariable String itemCode) {
        
        DictItem dictItem = dictItemService.getByTypeCodeAndItemCode(typeCode, itemCode);
        
        Map<String, Object> response = new HashMap<>();
        if (dictItem != null) {
            response.put("success", true);
            response.put("data", dictItem);
        } else {
            response.put("success", false);
            response.put("message", "字典项不存在");
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 查询子级字典项
     */
    @GetMapping("/children/{parentId}")
    public ResponseEntity<Map<String, Object>> getChildren(@PathVariable @NotNull Long parentId) {
        List<DictItem> children = dictItemService.getChildren(parentId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", children);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 新增字典项
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> save(@RequestBody @Valid DictItem dictItem) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean success = dictItemService.save(dictItem);
            response.put("success", success);
            response.put("message", success ? "保存成功" : "保存失败");
            if (success) {
                response.put("data", dictItem);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 更新字典项
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateById(
            @PathVariable @NotNull Long id,
            @RequestBody @Valid DictItem dictItem) {
        
        dictItem.setId(id);
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean success = dictItemService.updateById(dictItem);
            response.put("success", success);
            response.put("message", success ? "更新成功" : "更新失败");
            if (success) {
                response.put("data", dictItem);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 删除字典项
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteById(@PathVariable @NotNull Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean success = dictItemService.deleteById(id);
            response.put("success", success);
            response.put("message", success ? "删除成功" : "删除失败");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 批量删除字典项
     */
    @DeleteMapping("/batch")
    public ResponseEntity<Map<String, Object>> deleteBatch(@RequestBody @NotEmpty List<Long> ids) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean success = dictItemService.deleteBatch(ids);
            response.put("success", success);
            response.put("message", success ? "批量删除成功" : "批量删除失败");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 更新字典项状态
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateStatus(
            @PathVariable @NotNull Long id,
            @RequestParam String status) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean success = dictItemService.updateStatus(id, status);
            response.put("success", success);
            response.put("message", success ? "状态更新成功" : "状态更新失败");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 检查字典项编码是否存在
     */
    @GetMapping("/check-code")
    public ResponseEntity<Map<String, Object>> checkItemCode(
            @RequestParam Long typeId,
            @RequestParam String itemCode,
            @RequestParam(required = false) Long excludeId) {
        
        boolean exists = dictItemService.existsByItemCode(typeId, itemCode, excludeId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("exists", exists);
        response.put("message", exists ? "字典项编码已存在" : "字典项编码可用");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 根据字典值获取字典项名称
     */
    @GetMapping("/name-by-value")
    public ResponseEntity<Map<String, Object>> getItemNameByValue(
            @RequestParam String typeCode,
            @RequestParam String itemValue) {
        
        String itemName = dictItemService.getItemNameByValue(typeCode, itemValue);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", itemName);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 根据字典编码获取字典项名称
     */
    @GetMapping("/name-by-code")
    public ResponseEntity<Map<String, Object>> getItemNameByCode(
            @RequestParam String typeCode,
            @RequestParam String itemCode) {
        
        String itemName = dictItemService.getItemNameByCode(typeCode, itemCode);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", itemName);
        
        return ResponseEntity.ok(response);
    }
}