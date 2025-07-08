package org.max.cms.common.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.max.cms.common.entity.DictType;
import org.max.cms.common.repository.DictTypeRepository.DictTypeQuery;
import org.max.cms.common.service.DictTypeService;
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
 * 数据字典类型控制器
 */
@RestController
@RequestMapping("/api/dict-types")
@RequiredArgsConstructor
@Validated
public class DictTypeController {
    
    private final DictTypeService dictTypeService;
    
    /**
     * 分页查询字典类型
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String typeCode,
            @RequestParam(required = false) String typeName,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword) {
        
        Page<DictType> pageRequest = new Page<>(page, size);
        DictTypeQuery query = new DictTypeQuery();
        query.setTypeCode(typeCode);
        query.setTypeName(typeName);
        query.setStatus(status);
        query.setKeyword(keyword);
        
        IPage<DictType> result = dictTypeService.page(pageRequest, query);
        
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
     * 查询所有启用的字典类型
     */
    @GetMapping("/active")
    public ResponseEntity<Map<String, Object>> getAllActive() {
        List<DictType> dictTypes = dictTypeService.getAllActive();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", dictTypes);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 根据ID查询字典类型
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable @NotNull Long id) {
        DictType dictType = dictTypeService.getById(id);
        
        Map<String, Object> response = new HashMap<>();
        if (dictType != null) {
            response.put("success", true);
            response.put("data", dictType);
        } else {
            response.put("success", false);
            response.put("message", "字典类型不存在");
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 根据类型编码查询字典类型
     */
    @GetMapping("/code/{typeCode}")
    public ResponseEntity<Map<String, Object>> getByTypeCode(@PathVariable String typeCode) {
        DictType dictType = dictTypeService.getByTypeCode(typeCode);
        
        Map<String, Object> response = new HashMap<>();
        if (dictType != null) {
            response.put("success", true);
            response.put("data", dictType);
        } else {
            response.put("success", false);
            response.put("message", "字典类型不存在");
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 新增字典类型
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> save(@RequestBody @Valid DictType dictType) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean success = dictTypeService.save(dictType);
            response.put("success", success);
            response.put("message", success ? "保存成功" : "保存失败");
            if (success) {
                response.put("data", dictType);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 更新字典类型
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateById(
            @PathVariable @NotNull Long id,
            @RequestBody @Valid DictType dictType) {
        
        dictType.setId(id);
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean success = dictTypeService.updateById(dictType);
            response.put("success", success);
            response.put("message", success ? "更新成功" : "更新失败");
            if (success) {
                response.put("data", dictType);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 删除字典类型
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteById(@PathVariable @NotNull Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean success = dictTypeService.deleteById(id);
            response.put("success", success);
            response.put("message", success ? "删除成功" : "删除失败");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 批量删除字典类型
     */
    @DeleteMapping("/batch")
    public ResponseEntity<Map<String, Object>> deleteBatch(@RequestBody @NotEmpty List<Long> ids) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean success = dictTypeService.deleteBatch(ids);
            response.put("success", success);
            response.put("message", success ? "批量删除成功" : "批量删除失败");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 更新字典类型状态
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateStatus(
            @PathVariable @NotNull Long id,
            @RequestParam String status) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean success = dictTypeService.updateStatus(id, status);
            response.put("success", success);
            response.put("message", success ? "状态更新成功" : "状态更新失败");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 检查类型编码是否存在
     */
    @GetMapping("/check-code")
    public ResponseEntity<Map<String, Object>> checkTypeCode(
            @RequestParam String typeCode,
            @RequestParam(required = false) Long excludeId) {
        
        boolean exists = dictTypeService.existsByTypeCode(typeCode, excludeId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("exists", exists);
        response.put("message", exists ? "类型编码已存在" : "类型编码可用");
        
        return ResponseEntity.ok(response);
    }
}