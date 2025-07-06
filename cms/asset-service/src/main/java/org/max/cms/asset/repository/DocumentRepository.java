package org.max.cms.asset.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.max.cms.asset.entity.Document;

import java.util.List;

@Mapper
public interface DocumentRepository extends BaseMapper<Document> {
    
    /**
     * 根据文件夹ID查询文档列表
     * @param folderId 文件夹ID
     * @return 文档列表
     */
    List<Document> selectByFolderId(@Param("folderId") Long folderId);
    
    /**
     * 根据文件路径查询文档
     * @param filePath 文件路径
     * @return 文档
     */
    Document selectByFilePath(@Param("filePath") String filePath);
    
    /**
     * 查询所有未删除的文档
     * @return 文档列表
     */
    List<Document> selectAllActive();
    
    /**
     * 查询根目录下的文档（无文件夹）
     * @return 文档列表
     */
    List<Document> selectRootDocuments();
}