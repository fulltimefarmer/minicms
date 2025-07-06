package org.max.cms.asset.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.max.cms.asset.entity.Asset;

import java.util.List;

@Mapper
public interface AssetRepository extends BaseMapper<Asset> {
    
    /**
     * 根据用户ID查询资产列表
     * @param userId 用户ID
     * @return 资产列表
     */
    List<Asset> selectByUserId(@Param("userId") Long userId);
    
    /**
     * 查询未分配的资产列表
     * @return 未分配的资产列表
     */
    List<Asset> selectUnassignedAssets();
    
    /**
     * 将资产绑定到用户
     * @param assetId 资产ID
     * @param userId 用户ID
     * @return 更新的行数
     */
    int bindAssetToUser(@Param("assetId") Long assetId, @Param("userId") Long userId);
    
    /**
     * 解绑资产
     * @param assetId 资产ID
     * @return 更新的行数
     */
    int unbindAsset(@Param("assetId") Long assetId);
}