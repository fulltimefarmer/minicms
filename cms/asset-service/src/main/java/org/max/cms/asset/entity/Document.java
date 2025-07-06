package org.max.cms.asset.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.max.cms.common.entity.BaseEntity;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("documents")
public class Document extends BaseEntity {
    
    private Long folderId;
    private String name;
    private String originalName;
    private String filePath;
    private Long fileSize;
    private String mimeType;
    private String description;
}