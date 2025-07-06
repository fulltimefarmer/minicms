# 文档管理系统

本项目在asset-service中实现了完整的文档管理功能，支持文件夹管理和文档上传、下载等操作。

## 功能特性

### 文件夹管理
- ✅ 创建文件夹
- ✅ 更新文件夹信息
- ✅ 删除文件夹（仅当文件夹为空时）
- ✅ 查看文件夹详情
- ✅ 获取所有文件夹列表

### 文档管理
- ✅ 上传文档到指定文件夹或根目录
- ✅ 更新文档信息
- ✅ 删除文档（软删除，同时删除物理文件）
- ✅ 下载文档
- ✅ 查看文档详情
- ✅ 按文件夹查看文档列表
- ✅ 获取根目录文档列表
- ✅ 获取文档树结构

## 技术实现

### 数据库设计
- `folders` - 文件夹表
- `documents` - 文档表
- 支持软删除和审计字段

### 文件存储
- 默认存储路径：项目根目录下的 `files` 目录
- 支持通过配置文件自定义存储路径
- 文件名使用时间戳+UUID确保唯一性

### 权限控制
- 为admin用户配置了完整的文档管理权限
- 支持角色级别的权限控制

## API接口

### 文件夹管理

#### 创建文件夹
```
POST /api/documents/folders
Content-Type: application/json

{
  "name": "文件夹名称",
  "description": "文件夹描述"
}
```

#### 更新文件夹
```
PUT /api/documents/folders/{id}
Content-Type: application/json

{
  "name": "新的文件夹名称",
  "description": "新的文件夹描述"
}
```

#### 删除文件夹
```
DELETE /api/documents/folders/{id}
```

#### 获取文件夹详情
```
GET /api/documents/folders/{id}
```

#### 获取所有文件夹
```
GET /api/documents/folders
```

### 文档管理

#### 上传文档
```
POST /api/documents/upload
Content-Type: multipart/form-data

file: 文件
folderId: 文件夹ID（可选）
description: 文档描述（可选）
```

#### 更新文档信息
```
PUT /api/documents/{id}
Content-Type: application/json

{
  "name": "新的文档名称",
  "description": "新的文档描述"
}
```

#### 删除文档
```
DELETE /api/documents/{id}
```

#### 获取文档详情
```
GET /api/documents/{id}
```

#### 下载文档
```
GET /api/documents/download/{id}
```

#### 获取文件夹内的文档
```
GET /api/documents/folder/{folderId}
```

#### 获取根目录文档
```
GET /api/documents/root
```

#### 获取文档树结构
```
GET /api/documents/tree
```

## 配置说明

在 `application.yml` 中可以配置以下参数：

```yaml
document:
  storage-path: files  # 文件存储路径
  max-file-size: 10485760  # 最大文件大小（字节）
  allowed-types:  # 允许的文件类型
    - pdf
    - doc
    - docx
    - xls
    - xlsx
    - ppt
    - pptx
    - txt
    - jpg
    - jpeg
    - png
    - gif
    - bmp
    - zip
    - rar
```

## 用户权限

### Admin用户
- 用户名：`admin`
- 密码：`admin`
- 拥有系统最高权限，包括所有文档管理功能

### 数据库迁移
运行以下命令应用数据库迁移：
```bash
cd cms
mvn flyway:migrate
```

## 目录结构

```
cms/asset-service/
├── src/main/java/org/max/cms/asset/
│   ├── entity/
│   │   ├── Document.java          # 文档实体
│   │   └── Folder.java            # 文件夹实体
│   ├── repository/
│   │   ├── DocumentRepository.java # 文档数据访问层
│   │   └── FolderRepository.java   # 文件夹数据访问层
│   ├── service/
│   │   ├── DocumentService.java    # 文档服务接口
│   │   └── impl/
│   │       └── DocumentServiceImpl.java # 文档服务实现
│   ├── controller/
│   │   └── DocumentController.java # 文档管理控制器
│   └── config/
│       ├── DocumentConfig.java     # 文档配置
│       └── FileStorageConfig.java  # 文件存储配置
└── src/main/resources/
    └── application.yml             # 应用配置
```

## 使用说明

1. **启动服务**：确保asset-service正常启动
2. **创建文件夹**：首先创建需要的文件夹结构
3. **上传文档**：将文档上传到指定文件夹或根目录
4. **管理文档**：支持查看、编辑、删除文档
5. **下载文档**：通过API下载需要的文档

## 注意事项

- 文件上传大小限制为10MB（可通过配置修改）
- 仅支持配置中指定的文件类型
- 删除文件夹时，需要确保文件夹为空
- 文档删除为软删除，同时会删除物理文件
- 文件存储路径需要确保应用程序有读写权限