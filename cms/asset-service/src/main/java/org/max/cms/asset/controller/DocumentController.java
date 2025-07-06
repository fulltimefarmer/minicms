package org.max.cms.asset.controller;

// 移除swagger注解导入，使用简单注释
import lombok.extern.slf4j.Slf4j;
import org.max.cms.asset.entity.Document;
import org.max.cms.asset.entity.Folder;
import org.max.cms.asset.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/documents")
@Slf4j
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    // 文件夹管理接口
    @PostMapping("/folders")
    // 创建文件夹
    public ResponseEntity<Folder> createFolder(@RequestBody Folder folder) {
        try {
            Folder createdFolder = documentService.createFolder(folder);
            return ResponseEntity.ok(createdFolder);
        } catch (Exception e) {
            log.error("创建文件夹失败", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/folders/{id}")
    // 更新文件夹
    public ResponseEntity<Folder> updateFolder(@PathVariable Long id, @RequestBody Folder folder) {
        try {
            folder.setId(id);
            Folder updatedFolder = documentService.updateFolder(folder);
            return ResponseEntity.ok(updatedFolder);
        } catch (Exception e) {
            log.error("更新文件夹失败", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/folders/{id}")
    // 删除文件夹
    public ResponseEntity<Void> deleteFolder(@PathVariable Long id) {
        try {
            documentService.deleteFolder(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("删除文件夹失败", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/folders/{id}")
    // 根据ID获取文件夹
    public ResponseEntity<Folder> getFolderById(@PathVariable Long id) {
        try {
            Folder folder = documentService.getFolderById(id);
            if (folder != null) {
                return ResponseEntity.ok(folder);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("获取文件夹失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/folders")
    // 获取所有文件夹
    public ResponseEntity<List<Folder>> getAllFolders() {
        try {
            List<Folder> folders = documentService.getAllFolders();
            return ResponseEntity.ok(folders);
        } catch (Exception e) {
            log.error("获取文件夹列表失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 文档管理接口
    @PostMapping("/upload")
    // 上传文档
    public ResponseEntity<Document> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folderId", required = false) Long folderId,
            @RequestParam(value = "description", required = false) String description) {
        try {
            Document document = documentService.uploadDocument(file, folderId, description);
            return ResponseEntity.ok(document);
        } catch (Exception e) {
            log.error("上传文档失败", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}")
    // 更新文档信息
    public ResponseEntity<Document> updateDocument(@PathVariable Long id, @RequestBody Document document) {
        try {
            document.setId(id);
            Document updatedDocument = documentService.updateDocument(document);
            return ResponseEntity.ok(updatedDocument);
        } catch (Exception e) {
            log.error("更新文档失败", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{id}")
    // 删除文档
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        try {
            documentService.deleteDocument(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("删除文档失败", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/{id}")
    // 根据ID获取文档
    public ResponseEntity<Document> getDocumentById(@PathVariable Long id) {
        try {
            Document document = documentService.getDocumentById(id);
            if (document != null) {
                return ResponseEntity.ok(document);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("获取文档失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/folder/{folderId}")
    // 根据文件夹ID获取文档列表
    public ResponseEntity<List<Document>> getDocumentsByFolderId(@PathVariable Long folderId) {
        try {
            List<Document> documents = documentService.getDocumentsByFolderId(folderId);
            return ResponseEntity.ok(documents);
        } catch (Exception e) {
            log.error("获取文档列表失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/root")
    // 获取根目录下的文档
    public ResponseEntity<List<Document>> getRootDocuments() {
        try {
            List<Document> documents = documentService.getRootDocuments();
            return ResponseEntity.ok(documents);
        } catch (Exception e) {
            log.error("获取根目录文档失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/download/{id}")
    // 下载文档
    public ResponseEntity<byte[]> downloadDocument(@PathVariable Long id) {
        try {
            Document document = documentService.getDocumentById(id);
            if (document == null) {
                return ResponseEntity.notFound().build();
            }

            byte[] fileContent = documentService.downloadDocument(id);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", 
                URLEncoder.encode(document.getOriginalName(), StandardCharsets.UTF_8));
            
            return ResponseEntity.ok()
                .headers(headers)
                .body(fileContent);
        } catch (Exception e) {
            log.error("下载文档失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/tree")
    // 获取文档树结构
    public ResponseEntity<Map<String, Object>> getDocumentTree() {
        try {
            List<Folder> folders = documentService.getAllFolders();
            List<Document> rootDocuments = documentService.getRootDocuments();
            
            return ResponseEntity.ok(Map.of(
                "folders", folders,
                "rootDocuments", rootDocuments
            ));
        } catch (Exception e) {
            log.error("获取文档树失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}