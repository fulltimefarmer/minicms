-- 文档管理功能数据库迁移脚本
-- 创建文件夹和文档表

-- 文件夹表
CREATE TABLE folders (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255) DEFAULT 'system',
    updated_by VARCHAR(255) DEFAULT 'system',
    deleted BOOLEAN DEFAULT FALSE
);

-- 文档表
CREATE TABLE documents (
    id BIGSERIAL PRIMARY KEY,
    folder_id BIGINT,
    name VARCHAR(255) NOT NULL,
    original_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT NOT NULL,
    mime_type VARCHAR(100),
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255) DEFAULT 'system',
    updated_by VARCHAR(255) DEFAULT 'system',
    deleted BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (folder_id) REFERENCES folders(id)
);

-- 创建索引
CREATE INDEX idx_folders_name ON folders(name);
CREATE INDEX idx_documents_folder_id ON documents(folder_id);
CREATE INDEX idx_documents_name ON documents(name);
CREATE INDEX idx_documents_file_path ON documents(file_path);

-- 插入文档管理权限
INSERT INTO permissions (name, code, resource, action, description, type, path, sort) VALUES
('文档管理', 'DOCUMENT_MANAGEMENT', 'document', 'manage', '文档管理模块', 'MENU', '/documents', 7);

INSERT INTO permissions (name, code, resource, action, description, type, parent_id, sort) VALUES
('创建文件夹', 'FOLDER_CREATE', 'folder', 'create', '创建新文件夹', 'API', (SELECT id FROM permissions WHERE code = 'DOCUMENT_MANAGEMENT'), 1),
('查看文件夹', 'FOLDER_READ', 'folder', 'read', '查看文件夹信息', 'API', (SELECT id FROM permissions WHERE code = 'DOCUMENT_MANAGEMENT'), 2),
('编辑文件夹', 'FOLDER_UPDATE', 'folder', 'update', '编辑文件夹信息', 'API', (SELECT id FROM permissions WHERE code = 'DOCUMENT_MANAGEMENT'), 3),
('删除文件夹', 'FOLDER_DELETE', 'folder', 'delete', '删除文件夹', 'API', (SELECT id FROM permissions WHERE code = 'DOCUMENT_MANAGEMENT'), 4),
('上传文档', 'DOCUMENT_UPLOAD', 'document', 'upload', '上传文档', 'API', (SELECT id FROM permissions WHERE code = 'DOCUMENT_MANAGEMENT'), 5),
('下载文档', 'DOCUMENT_DOWNLOAD', 'document', 'download', '下载文档', 'API', (SELECT id FROM permissions WHERE code = 'DOCUMENT_MANAGEMENT'), 6),
('查看文档', 'DOCUMENT_READ', 'document', 'read', '查看文档信息', 'API', (SELECT id FROM permissions WHERE code = 'DOCUMENT_MANAGEMENT'), 7),
('编辑文档', 'DOCUMENT_UPDATE', 'document', 'update', '编辑文档信息', 'API', (SELECT id FROM permissions WHERE code = 'DOCUMENT_MANAGEMENT'), 8),
('删除文档', 'DOCUMENT_DELETE', 'document', 'delete', '删除文档', 'API', (SELECT id FROM permissions WHERE code = 'DOCUMENT_MANAGEMENT'), 9);

-- 为超级管理员分配所有文档管理权限
INSERT INTO role_permissions (role_id, permission_id)
SELECT 
    (SELECT id FROM roles WHERE code = 'SUPER_ADMIN'),
    p.id
FROM permissions p
WHERE p.code IN (
    'DOCUMENT_MANAGEMENT',
    'FOLDER_CREATE', 'FOLDER_READ', 'FOLDER_UPDATE', 'FOLDER_DELETE',
    'DOCUMENT_UPLOAD', 'DOCUMENT_DOWNLOAD', 'DOCUMENT_READ', 'DOCUMENT_UPDATE', 'DOCUMENT_DELETE'
);

-- 为管理员分配文档管理权限
INSERT INTO role_permissions (role_id, permission_id)
SELECT 
    (SELECT id FROM roles WHERE code = 'ADMIN'),
    p.id
FROM permissions p
WHERE p.code IN (
    'DOCUMENT_MANAGEMENT',
    'FOLDER_CREATE', 'FOLDER_READ', 'FOLDER_UPDATE', 'FOLDER_DELETE',
    'DOCUMENT_UPLOAD', 'DOCUMENT_DOWNLOAD', 'DOCUMENT_READ', 'DOCUMENT_UPDATE', 'DOCUMENT_DELETE'
);

-- 更新admin用户密码为 "admin" (BCrypt加密)
UPDATE users 
SET password = '$2a$10$DowJonesJE8JdLMdJqK0HuEbCUGMd8/7tXxPzLEFLPZFVNIUVXRQC' 
WHERE username = 'admin';

-- 插入示例文件夹
INSERT INTO folders (name, description, created_by, updated_by) VALUES
('公司文档', '公司重要文档存储', 'admin', 'admin'),
('技术文档', '技术相关文档', 'admin', 'admin'),
('财务文档', '财务相关文档', 'admin', 'admin'),
('人事文档', '人事相关文档', 'admin', 'admin');