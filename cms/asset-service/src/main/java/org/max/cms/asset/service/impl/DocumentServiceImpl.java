package org.max.cms.asset.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.max.cms.asset.config.DocumentConfig;
import org.max.cms.asset.entity.Document;
import org.max.cms.asset.entity.Folder;
import org.max.cms.asset.repository.DocumentRepository;
import org.max.cms.asset.repository.FolderRepository;
import org.max.cms.asset.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@Slf4j
public class DocumentServiceImpl implements DocumentService {

    @Autowired
    private FolderRepository folderRepository;
    
    @Autowired
    private DocumentRepository documentRepository;
    
    @Autowired
    private DocumentConfig documentConfig;

    // 文件夹管理
    @Override
    public Folder createFolder(Folder folder) {
        // 检查同名文件夹是否已存在
        QueryWrapper<Folder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", folder.getName())
                   .eq("deleted", false);
        
        if (folderRepository.selectOne(queryWrapper) != null) {
            throw new RuntimeException("文件夹名称已存在");
        }
        
        folderRepository.insert(folder);
        return folder;
    }

    @Override
    public Folder updateFolder(Folder folder) {
        Folder existingFolder = folderRepository.selectById(folder.getId());
        if (existingFolder == null || existingFolder.getDeleted()) {
            throw new RuntimeException("文件夹不存在");
        }
        
        // 检查同名文件夹是否已存在（排除当前文件夹）
        QueryWrapper<Folder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", folder.getName())
                   .eq("deleted", false)
                   .ne("id", folder.getId());
        
        if (folderRepository.selectOne(queryWrapper) != null) {
            throw new RuntimeException("文件夹名称已存在");
        }
        
        folderRepository.updateById(folder);
        return folder;
    }

    @Override
    public void deleteFolder(Long folderId) {
        Folder folder = folderRepository.selectById(folderId);
        if (folder == null || folder.getDeleted()) {
            throw new RuntimeException("文件夹不存在");
        }
        
        // 检查文件夹下是否有文档
        QueryWrapper<Document> docQueryWrapper = new QueryWrapper<>();
        docQueryWrapper.eq("folder_id", folderId)
                      .eq("deleted", false);
        
        List<Document> documents = documentRepository.selectList(docQueryWrapper);
        if (!documents.isEmpty()) {
            throw new RuntimeException("文件夹下还有文档，无法删除");
        }
        
        // 软删除
        folder.setDeleted(true);
        folderRepository.updateById(folder);
    }

    @Override
    public Folder getFolderById(Long folderId) {
        Folder folder = folderRepository.selectById(folderId);
        if (folder == null || folder.getDeleted()) {
            return null;
        }
        return folder;
    }

    @Override
    public List<Folder> getAllFolders() {
        QueryWrapper<Folder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", false)
                   .orderByDesc("created_at");
        return folderRepository.selectList(queryWrapper);
    }

    // 文档管理
    @Override
    public Document uploadDocument(MultipartFile file, Long folderId, String description) {
        if (file.isEmpty()) {
            throw new RuntimeException("文件不能为空");
        }
        
        // 检查文件大小
        if (file.getSize() > documentConfig.getMaxFileSize()) {
            throw new RuntimeException("文件大小超出限制");
        }
        
        // 检查文件类型
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        if (!isAllowedFileType(extension)) {
            throw new RuntimeException("不支持的文件类型");
        }
        
        // 检查文件夹是否存在
        if (folderId != null) {
            Folder folder = getFolderById(folderId);
            if (folder == null) {
                throw new RuntimeException("文件夹不存在");
            }
        }
        
        try {
            // 创建存储目录
            Path uploadPath = Paths.get(documentConfig.getStoragePath());
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // 生成唯一文件名
            String uniqueFileName = generateUniqueFileName(originalFilename);
            Path filePath = uploadPath.resolve(uniqueFileName);
            
            // 保存文件
            file.transferTo(filePath.toFile());
            
            // 创建文档记录
            Document document = Document.builder()
                .folderId(folderId)
                .name(originalFilename)
                .originalName(originalFilename)
                .filePath(filePath.toString())
                .fileSize(file.getSize())
                .mimeType(file.getContentType())
                .description(description)
                .build();
            
            documentRepository.insert(document);
            return document;
            
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new RuntimeException("文件上传失败");
        }
    }

    @Override
    public Document updateDocument(Document document) {
        Document existingDocument = documentRepository.selectById(document.getId());
        if (existingDocument == null || existingDocument.getDeleted()) {
            throw new RuntimeException("文档不存在");
        }
        
        documentRepository.updateById(document);
        return document;
    }

    @Override
    public void deleteDocument(Long documentId) {
        Document document = documentRepository.selectById(documentId);
        if (document == null || document.getDeleted()) {
            throw new RuntimeException("文档不存在");
        }
        
        try {
            // 删除物理文件
            Path filePath = Paths.get(document.getFilePath());
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
            
            // 软删除记录
            document.setDeleted(true);
            documentRepository.updateById(document);
            
        } catch (IOException e) {
            log.error("删除文件失败", e);
            throw new RuntimeException("删除文件失败");
        }
    }

    @Override
    public Document getDocumentById(Long documentId) {
        Document document = documentRepository.selectById(documentId);
        if (document == null || document.getDeleted()) {
            return null;
        }
        return document;
    }

    @Override
    public List<Document> getDocumentsByFolderId(Long folderId) {
        QueryWrapper<Document> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("folder_id", folderId)
                   .eq("deleted", false)
                   .orderByDesc("created_at");
        return documentRepository.selectList(queryWrapper);
    }

    @Override
    public List<Document> getRootDocuments() {
        QueryWrapper<Document> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNull("folder_id")
                   .eq("deleted", false)
                   .orderByDesc("created_at");
        return documentRepository.selectList(queryWrapper);
    }

    @Override
    public byte[] downloadDocument(Long documentId) {
        Document document = getDocumentById(documentId);
        if (document == null) {
            throw new RuntimeException("文档不存在");
        }
        
        try {
            Path filePath = Paths.get(document.getFilePath());
            if (!Files.exists(filePath)) {
                throw new RuntimeException("文件不存在");
            }
            
            return Files.readAllBytes(filePath);
            
        } catch (IOException e) {
            log.error("读取文件失败", e);
            throw new RuntimeException("读取文件失败");
        }
    }

    // 私有方法
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1).toLowerCase();
    }

    private boolean isAllowedFileType(String extension) {
        if (extension == null || extension.isEmpty()) {
            return false;
        }
        
        for (String allowedType : documentConfig.getAllowedTypes()) {
            if (allowedType.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }

    private String generateUniqueFileName(String originalFilename) {
        String extension = getFileExtension(originalFilename);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uuid = UUID.randomUUID().toString().replace("-", "");
        
        return timestamp + "_" + uuid + "." + extension;
    }
}