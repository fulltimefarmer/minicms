package org.max.cms.asset.controller;

import org.max.cms.asset.entity.Asset;
import org.max.cms.asset.service.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assets")
@CrossOrigin(origins = "*")
public class AssetController {
    
    @Autowired
    private AssetService assetService;
    
    /**
     * 获取所有资产
     * @return 资产列表
     */
    @GetMapping
    public ResponseEntity<List<Asset>> getAllAssets() {
        List<Asset> assets = assetService.getAllAssets();
        return ResponseEntity.ok(assets);
    }
    
    /**
     * 根据用户ID获取资产列表
     * @param userId 用户ID
     * @return 资产列表
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Asset>> getAssetsByUserId(@PathVariable Long userId) {
        List<Asset> assets = assetService.getAssetsByUserId(userId);
        return ResponseEntity.ok(assets);
    }
    
    /**
     * 获取未分配的资产列表
     * @return 未分配的资产列表
     */
    @GetMapping("/unassigned")
    public ResponseEntity<List<Asset>> getUnassignedAssets() {
        List<Asset> assets = assetService.getUnassignedAssets();
        return ResponseEntity.ok(assets);
    }
    
    /**
     * 将资产绑定到用户
     * @param assetId 资产ID
     * @param userId 用户ID
     * @return 操作结果
     */
    @PostMapping("/{assetId}/bind/{userId}")
    public ResponseEntity<String> bindAssetToUser(@PathVariable Long assetId, @PathVariable Long userId) {
        try {
            boolean success = assetService.bindAssetToUser(assetId, userId);
            if (success) {
                return ResponseEntity.ok("资产绑定成功");
            } else {
                return ResponseEntity.badRequest().body("资产绑定失败");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * 解绑资产
     * @param assetId 资产ID
     * @return 操作结果
     */
    @PostMapping("/{assetId}/unbind")
    public ResponseEntity<String> unbindAsset(@PathVariable Long assetId) {
        try {
            boolean success = assetService.unbindAsset(assetId);
            if (success) {
                return ResponseEntity.ok("资产解绑成功");
            } else {
                return ResponseEntity.badRequest().body("资产解绑失败");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * 创建资产
     * @param asset 资产信息
     * @return 创建的资产
     */
    @PostMapping
    public ResponseEntity<Asset> createAsset(@RequestBody Asset asset) {
        Asset createdAsset = assetService.createAsset(asset);
        return ResponseEntity.ok(createdAsset);
    }
    
    /**
     * 更新资产
     * @param id 资产ID
     * @param asset 资产信息
     * @return 更新的资产
     */
    @PutMapping("/{id}")
    public ResponseEntity<Asset> updateAsset(@PathVariable Long id, @RequestBody Asset asset) {
        asset.setId(id);
        Asset updatedAsset = assetService.updateAsset(asset);
        return ResponseEntity.ok(updatedAsset);
    }
    
    /**
     * 删除资产
     * @param id 资产ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAsset(@PathVariable Long id) {
        boolean success = assetService.deleteAsset(id);
        if (success) {
            return ResponseEntity.ok("资产删除成功");
        } else {
            return ResponseEntity.badRequest().body("资产删除失败");
        }
    }
}