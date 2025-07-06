package org.max.cms.asset.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.max.cms.asset.entity.Folder;

import java.util.List;

@Mapper
public interface FolderRepository extends BaseMapper<Folder> {
    
    /**
     * 根据名称查询文件夹
     * @param name 文件夹名称
     * @return 文件夹
     */
    Folder selectByName(@Param("name") String name);
    
    /**
     * 查询所有未删除的文件夹
     * @return 文件夹列表
     */
    List<Folder> selectAllActive();
}