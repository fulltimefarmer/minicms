package org.max.cms.asset.service;

import org.max.cms.asset.entity.Document;
import org.max.cms.asset.entity.Folder;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocumentService {
    
    // 文件夹管理
    /**
     * 创建文件夹
     */
    Folder createFolder(Folder folder);
    
    /**
     * 更新文件夹
     */
    Folder updateFolder(Folder folder);
    
    /**
     * 删除文件夹
     */
    void deleteFolder(Long folderId);
    
    /**
     * 根据ID获取文件夹
     */
    Folder getFolderById(Long folderId);
    
    /**
     * 获取所有文件夹
     */
    List<Folder> getAllFolders();
    
    // 文档管理
    /**
     * 上传文档
     */
    Document uploadDocument(MultipartFile file, Long folderId, String description);
    
    /**
     * 更新文档信息
     */
    Document updateDocument(Document document);
    
    /**
     * 删除文档
     */
    void deleteDocument(Long documentId);
    
    /**
     * 根据ID获取文档
     */
    Document getDocumentById(Long documentId);
    
    /**
     * 根据文件夹ID获取文档列表
     */
    List<Document> getDocumentsByFolderId(Long folderId);
    
    /**
     * 获取根目录下的文档
     */
    List<Document> getRootDocuments();
    
    /**
     * 下载文档
     */
    byte[] downloadDocument(Long documentId);
}