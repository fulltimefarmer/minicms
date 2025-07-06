package org.max.cms.asset.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.max.cms.asset.entity.Asset;
import org.max.cms.asset.repository.AssetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AssetService extends ServiceImpl<AssetRepository, Asset> {
    
    /**
     * 获取所有资产
     * @return 资产列表
     */
    public List<Asset> getAllAssets() {
        return list(new QueryWrapper<Asset>().eq("deleted", false));
    }
    
    /**
     * 根据用户ID获取资产列表
     * @param userId 用户ID
     * @return 资产列表
     */
    public List<Asset> getAssetsByUserId(Long userId) {
        return baseMapper.selectByUserId(userId);
    }
    
    /**
     * 获取未分配的资产列表
     * @return 未分配的资产列表
     */
    public List<Asset> getUnassignedAssets() {
        return baseMapper.selectUnassignedAssets();
    }
    
    /**
     * 将资产绑定到用户
     * @param assetId 资产ID
     * @param userId 用户ID
     * @return 是否成功
     */
    public boolean bindAssetToUser(Long assetId, Long userId) {
        // 检查资产是否存在且未被删除
        Asset asset = getById(assetId);
        if (asset == null || asset.getDeleted()) {
            throw new RuntimeException("资产不存在或已删除");
        }
        
        // 检查资产是否已绑定其他用户
        if (asset.getUserId() != null && !asset.getUserId().equals(userId)) {
            throw new RuntimeException("资产已绑定到其他用户");
        }
        
        return baseMapper.bindAssetToUser(assetId, userId) > 0;
    }
    
    /**
     * 解绑资产
     * @param assetId 资产ID
     * @return 是否成功
     */
    public boolean unbindAsset(Long assetId) {
        return baseMapper.unbindAsset(assetId) > 0;
    }
    
    /**
     * 创建资产
     * @param asset 资产信息
     * @return 创建的资产
     */
    public Asset createAsset(Asset asset) {
        save(asset);
        return asset;
    }
    
    /**
     * 更新资产
     * @param asset 资产信息
     * @return 更新的资产
     */
    public Asset updateAsset(Asset asset) {
        updateById(asset);
        return asset;
    }
    
    /**
     * 删除资产
     * @param assetId 资产ID
     * @return 是否成功
     */
    public boolean deleteAsset(Long assetId) {
        return removeById(assetId);
    }
}