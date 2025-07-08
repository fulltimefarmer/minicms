-- 初始化数据脚本
-- 插入系统运行所需的基础数据

-- 插入默认角色
INSERT INTO roles (name, code, description, level) VALUES
('超级管理员', 'SUPER_ADMIN', '拥有系统所有权限的超级管理员', 1),
('管理员', 'ADMIN', '系统管理员，拥有大部分管理权限', 2),
('部门经理', 'MANAGER', '部门经理，拥有部门内权限', 3),
('编辑者', 'EDITOR', '内容编辑者，可以编辑相关内容', 4),
('普通用户', 'USER', '普通用户，拥有基本权限', 5),
('访客', 'GUEST', '访客用户，仅有查看权限', 6);

-- 插入默认权限 - 系统管理模块
INSERT INTO permissions (name, code, resource, action, description, type, path, sort) VALUES
('系统管理', 'SYSTEM_MANAGEMENT', 'system', 'manage', '系统管理模块', 'MENU', '/system', 1),
('用户管理', 'USER_MANAGEMENT', 'user', 'manage', '用户管理模块', 'MENU', '/system/users', 2),
('角色管理', 'ROLE_MANAGEMENT', 'role', 'manage', '角色管理模块', 'MENU', '/system/roles', 3),
('权限管理', 'PERMISSION_MANAGEMENT', 'permission', 'manage', '权限管理模块', 'MENU', '/system/permissions', 4),
('审计日志', 'AUDIT_LOG_MANAGEMENT', 'audit', 'manage', '审计日志管理模块', 'MENU', '/system/audit-logs', 5);

-- 插入用户管理权限
INSERT INTO permissions (name, code, resource, action, description, type, parent_id, sort) VALUES
('创建用户', 'USER_CREATE', 'user', 'create', '创建新用户', 'API', (SELECT id FROM permissions WHERE code = 'USER_MANAGEMENT'), 1),
('查看用户', 'USER_READ', 'user', 'read', '查看用户信息', 'API', (SELECT id FROM permissions WHERE code = 'USER_MANAGEMENT'), 2),
('编辑用户', 'USER_UPDATE', 'user', 'update', '编辑用户信息', 'API', (SELECT id FROM permissions WHERE code = 'USER_MANAGEMENT'), 3),
('删除用户', 'USER_DELETE', 'user', 'delete', '删除用户', 'API', (SELECT id FROM permissions WHERE code = 'USER_MANAGEMENT'), 4),
('重置密码', 'USER_RESET_PASSWORD', 'user', 'reset_password', '重置用户密码', 'API', (SELECT id FROM permissions WHERE code = 'USER_MANAGEMENT'), 5),
('分配角色', 'USER_ASSIGN_ROLES', 'user', 'assign_roles', '为用户分配角色', 'API', (SELECT id FROM permissions WHERE code = 'USER_MANAGEMENT'), 6);

-- 插入角色管理权限
INSERT INTO permissions (name, code, resource, action, description, type, parent_id, sort) VALUES
('创建角色', 'ROLE_CREATE', 'role', 'create', '创建新角色', 'API', (SELECT id FROM permissions WHERE code = 'ROLE_MANAGEMENT'), 1),
('查看角色', 'ROLE_READ', 'role', 'read', '查看角色信息', 'API', (SELECT id FROM permissions WHERE code = 'ROLE_MANAGEMENT'), 2),
('编辑角色', 'ROLE_UPDATE', 'role', 'update', '编辑角色信息', 'API', (SELECT id FROM permissions WHERE code = 'ROLE_MANAGEMENT'), 3),
('删除角色', 'ROLE_DELETE', 'role', 'delete', '删除角色', 'API', (SELECT id FROM permissions WHERE code = 'ROLE_MANAGEMENT'), 4),
('分配权限', 'ROLE_ASSIGN_PERMISSIONS', 'role', 'assign_permissions', '为角色分配权限', 'API', (SELECT id FROM permissions WHERE code = 'ROLE_MANAGEMENT'), 5);

-- 插入权限管理权限
INSERT INTO permissions (name, code, resource, action, description, type, parent_id, sort) VALUES
('创建权限', 'PERMISSION_CREATE', 'permission', 'create', '创建新权限', 'API', (SELECT id FROM permissions WHERE code = 'PERMISSION_MANAGEMENT'), 1),
('查看权限', 'PERMISSION_READ', 'permission', 'read', '查看权限信息', 'API', (SELECT id FROM permissions WHERE code = 'PERMISSION_MANAGEMENT'), 2),
('编辑权限', 'PERMISSION_UPDATE', 'permission', 'update', '编辑权限信息', 'API', (SELECT id FROM permissions WHERE code = 'PERMISSION_MANAGEMENT'), 3),
('删除权限', 'PERMISSION_DELETE', 'permission', 'delete', '删除权限', 'API', (SELECT id FROM permissions WHERE code = 'PERMISSION_MANAGEMENT'), 4);

-- 插入审计日志权限
INSERT INTO permissions (name, code, resource, action, description, type, parent_id, sort) VALUES
('查看审计日志', 'AUDIT_LOG_READ', 'audit', 'read', '查看审计日志', 'API', (SELECT id FROM permissions WHERE code = 'AUDIT_LOG_MANAGEMENT'), 1),
('导出审计日志', 'AUDIT_LOG_EXPORT', 'audit', 'export', '导出审计日志', 'API', (SELECT id FROM permissions WHERE code = 'AUDIT_LOG_MANAGEMENT'), 2);

-- 插入资产管理权限
INSERT INTO permissions (name, code, resource, action, description, type, path, sort) VALUES
('资产管理', 'ASSET_MANAGEMENT', 'asset', 'manage', '资产管理模块', 'MENU', '/assets', 6);

INSERT INTO permissions (name, code, resource, action, description, type, parent_id, sort) VALUES
('创建资产', 'ASSET_CREATE', 'asset', 'create', '创建新资产', 'API', (SELECT id FROM permissions WHERE code = 'ASSET_MANAGEMENT'), 1),
('查看资产', 'ASSET_READ', 'asset', 'read', '查看资产信息', 'API', (SELECT id FROM permissions WHERE code = 'ASSET_MANAGEMENT'), 2),
('编辑资产', 'ASSET_UPDATE', 'asset', 'update', '编辑资产信息', 'API', (SELECT id FROM permissions WHERE code = 'ASSET_MANAGEMENT'), 3),
('删除资产', 'ASSET_DELETE', 'asset', 'delete', '删除资产', 'API', (SELECT id FROM permissions WHERE code = 'ASSET_MANAGEMENT'), 4),
('导出资产', 'ASSET_EXPORT', 'asset', 'export', '导出资产清单', 'API', (SELECT id FROM permissions WHERE code = 'ASSET_MANAGEMENT'), 5);

-- 为超级管理员分配所有权限
INSERT INTO role_permissions (role_id, permission_id)
SELECT 
    (SELECT id FROM roles WHERE code = 'SUPER_ADMIN'),
    p.id
FROM permissions p;

-- 为管理员分配基本管理权限
INSERT INTO role_permissions (role_id, permission_id)
SELECT 
    (SELECT id FROM roles WHERE code = 'ADMIN'),
    p.id
FROM permissions p
WHERE p.code IN (
    'SYSTEM_MANAGEMENT',
    'USER_MANAGEMENT', 'USER_CREATE', 'USER_READ', 'USER_UPDATE', 'USER_ASSIGN_ROLES',
    'ROLE_MANAGEMENT', 'ROLE_READ', 'ROLE_UPDATE',
    'PERMISSION_MANAGEMENT', 'PERMISSION_READ',
    'AUDIT_LOG_MANAGEMENT', 'AUDIT_LOG_READ', 'AUDIT_LOG_EXPORT',
    'ASSET_MANAGEMENT', 'ASSET_CREATE', 'ASSET_READ', 'ASSET_UPDATE', 'ASSET_DELETE', 'ASSET_EXPORT'
);

-- 为部门经理分配部门权限
INSERT INTO role_permissions (role_id, permission_id)
SELECT 
    (SELECT id FROM roles WHERE code = 'MANAGER'),
    p.id
FROM permissions p
WHERE p.code IN (
    'USER_MANAGEMENT', 'USER_READ', 'USER_UPDATE',
    'ASSET_MANAGEMENT', 'ASSET_CREATE', 'ASSET_READ', 'ASSET_UPDATE', 'ASSET_EXPORT'
);

-- 为编辑者分配编辑权限
INSERT INTO role_permissions (role_id, permission_id)
SELECT 
    (SELECT id FROM roles WHERE code = 'EDITOR'),
    p.id
FROM permissions p
WHERE p.code IN (
    'USER_READ',
    'ASSET_MANAGEMENT', 'ASSET_CREATE', 'ASSET_READ', 'ASSET_UPDATE'
);

-- 为普通用户分配基本权限
INSERT INTO role_permissions (role_id, permission_id)
SELECT 
    (SELECT id FROM roles WHERE code = 'USER'),
    p.id
FROM permissions p
WHERE p.code IN ('ASSET_READ');

-- 为访客分配查看权限
INSERT INTO role_permissions (role_id, permission_id)
SELECT 
    (SELECT id FROM roles WHERE code = 'GUEST'),
    p.id
FROM permissions p
WHERE p.code IN ('ASSET_READ');

-- 插入默认用户 (密码使用BCrypt加密，原始密码为"1234")
INSERT INTO users (username, email, password, first_name, last_name, nickname, enabled) VALUES
('admin', 'admin@company.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye3r/z9YEXoJ9.gvCb3q4bkdyJ/1OG3m2', 'System', 'Administrator', '系统管理员', true),
('manager', 'manager@company.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye3r/z9YEXoJ9.gvCb3q4bkdyJ/1OG3m2', 'Department', 'Manager', '部门经理', true),
('editor', 'editor@company.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye3r/z9YEXoJ9.gvCb3q4bkdyJ/1OG3m2', 'Content', 'Editor', '内容编辑', true),
('user', 'user@company.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye3r/z9YEXoJ9.gvCb3q4bkdyJ/1OG3m2', 'Regular', 'User', '普通用户', true);

-- 分配用户角色
INSERT INTO user_roles (user_id, role_id) VALUES
((SELECT id FROM users WHERE username = 'admin'), (SELECT id FROM roles WHERE code = 'SUPER_ADMIN')),
((SELECT id FROM users WHERE username = 'manager'), (SELECT id FROM roles WHERE code = 'MANAGER')),
((SELECT id FROM users WHERE username = 'editor'), (SELECT id FROM roles WHERE code = 'EDITOR')),
((SELECT id FROM users WHERE username = 'user'), (SELECT id FROM roles WHERE code = 'USER'));

-- 插入示例资产数据
INSERT INTO assets (name, asset_number, type, category, description, brand, model, status, location, department, responsible_person) VALUES
('MacBook Pro 13"', 'LAPTOP-001', 'Laptop', 'IT Equipment', 'Apple MacBook Pro 13-inch 2023', 'Apple', 'MacBook Pro 13"', 'ACTIVE', 'Office Floor 1', 'IT Department', 'John Smith'),
('MacBook Pro 15"', 'LAPTOP-002', 'Laptop', 'IT Equipment', 'Apple MacBook Pro 15-inch 2023', 'Apple', 'MacBook Pro 15"', 'ACTIVE', 'Office Floor 1', 'IT Department', 'Jane Doe'),
('Dell Monitor 27"', 'MONITOR-001', 'Monitor', 'IT Equipment', 'Dell 27-inch 4K Monitor', 'Dell', 'U2720Q', 'ACTIVE', 'Office Floor 1', 'IT Department', 'John Smith'),
('Dell Monitor 24"', 'MONITOR-002', 'Monitor', 'IT Equipment', 'Dell 24-inch Monitor', 'Dell', 'U2414H', 'ACTIVE', 'Office Floor 2', 'IT Department', 'Jane Doe'),
('iPhone 15 Pro', 'PHONE-001', 'Mobile Phone', 'IT Equipment', 'Apple iPhone 15 Pro 256GB', 'Apple', 'iPhone 15 Pro', 'ACTIVE', 'Office Floor 1', 'Sales Department', 'Mike Johnson'),
('Office Chair Premium', 'FURNITURE-001', 'Furniture', 'Office Furniture', 'Ergonomic office chair with lumbar support', 'Herman Miller', 'Aeron', 'ACTIVE', 'Office Floor 2', 'HR Department', 'Sarah Wilson'),
('Standing Desk', 'FURNITURE-002', 'Furniture', 'Office Furniture', 'Height adjustable standing desk', 'IKEA', 'Bekant', 'ACTIVE', 'Office Floor 1', 'IT Department', 'John Smith'),
('Printer HP LaserJet', 'PRINTER-001', 'Printer', 'IT Equipment', 'HP LaserJet Pro M404dn', 'HP', 'LaserJet Pro M404dn', 'ACTIVE', 'Office Floor 1', 'Admin Department', 'Admin Staff'),
('Projector', 'PROJECTOR-001', 'Projector', 'IT Equipment', 'Epson PowerLite Conference Room Projector', 'Epson', 'PowerLite 1795F', 'ACTIVE', 'Conference Room A', 'IT Department', 'IT Support'),
('Whiteboard', 'FURNITURE-003', 'Furniture', 'Office Furniture', 'Magnetic whiteboard 120x90cm', 'Generic', 'Standard', 'ACTIVE', 'Conference Room B', 'Admin Department', 'Admin Staff');