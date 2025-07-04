-- 插入默认角色
INSERT INTO roles (name, code, description, level) VALUES
('Super Administrator', 'SUPER_ADMIN', 'System super administrator with all permissions', 1),
('Administrator', 'ADMIN', 'System administrator', 2),
('Manager', 'MANAGER', 'Department manager', 3),
('User', 'USER', 'Regular user', 4),
('Guest', 'GUEST', 'Guest user with limited access', 5);

-- 插入默认权限 - 系统管理
INSERT INTO permissions (name, code, resource, action, description, type, path, sort) VALUES
('System Management', 'SYSTEM_MANAGEMENT', 'system', 'manage', 'System management module', 'MENU', '/system', 1),
('User Management', 'USER_MANAGEMENT', 'user', 'manage', 'User management', 'MENU', '/system/users', 2),
('Role Management', 'ROLE_MANAGEMENT', 'role', 'manage', 'Role management', 'MENU', '/system/roles', 3),
('Permission Management', 'PERMISSION_MANAGEMENT', 'permission', 'manage', 'Permission management', 'MENU', '/system/permissions', 4),
('Audit Log Management', 'AUDIT_LOG_MANAGEMENT', 'audit', 'manage', 'Audit log management', 'MENU', '/system/audit-logs', 5);

-- 插入用户管理权限
INSERT INTO permissions (name, code, resource, action, description, type, parent_id, sort) VALUES
('Create User', 'USER_CREATE', 'user', 'create', 'Create new user', 'API', (SELECT id FROM permissions WHERE code = 'USER_MANAGEMENT'), 1),
('Read User', 'USER_READ', 'user', 'read', 'View user information', 'API', (SELECT id FROM permissions WHERE code = 'USER_MANAGEMENT'), 2),
('Update User', 'USER_UPDATE', 'user', 'update', 'Update user information', 'API', (SELECT id FROM permissions WHERE code = 'USER_MANAGEMENT'), 3),
('Delete User', 'USER_DELETE', 'user', 'delete', 'Delete user', 'API', (SELECT id FROM permissions WHERE code = 'USER_MANAGEMENT'), 4),
('Reset Password', 'USER_RESET_PASSWORD', 'user', 'reset_password', 'Reset user password', 'API', (SELECT id FROM permissions WHERE code = 'USER_MANAGEMENT'), 5);

-- 插入角色管理权限
INSERT INTO permissions (name, code, resource, action, description, type, parent_id, sort) VALUES
('Create Role', 'ROLE_CREATE', 'role', 'create', 'Create new role', 'API', (SELECT id FROM permissions WHERE code = 'ROLE_MANAGEMENT'), 1),
('Read Role', 'ROLE_READ', 'role', 'read', 'View role information', 'API', (SELECT id FROM permissions WHERE code = 'ROLE_MANAGEMENT'), 2),
('Update Role', 'ROLE_UPDATE', 'role', 'update', 'Update role information', 'API', (SELECT id FROM permissions WHERE code = 'ROLE_MANAGEMENT'), 3),
('Delete Role', 'ROLE_DELETE', 'role', 'delete', 'Delete role', 'API', (SELECT id FROM permissions WHERE code = 'ROLE_MANAGEMENT'), 4),
('Assign Permissions', 'ROLE_ASSIGN_PERMISSIONS', 'role', 'assign_permissions', 'Assign permissions to role', 'API', (SELECT id FROM permissions WHERE code = 'ROLE_MANAGEMENT'), 5);

-- 插入资产管理权限
INSERT INTO permissions (name, code, resource, action, description, type, path, sort) VALUES
('Asset Management', 'ASSET_MANAGEMENT', 'asset', 'manage', 'Asset management module', 'MENU', '/assets', 6);

INSERT INTO permissions (name, code, resource, action, description, type, parent_id, sort) VALUES
('Create Asset', 'ASSET_CREATE', 'asset', 'create', 'Create new asset', 'API', (SELECT id FROM permissions WHERE code = 'ASSET_MANAGEMENT'), 1),
('Read Asset', 'ASSET_READ', 'asset', 'read', 'View asset information', 'API', (SELECT id FROM permissions WHERE code = 'ASSET_MANAGEMENT'), 2),
('Update Asset', 'ASSET_UPDATE', 'asset', 'update', 'Update asset information', 'API', (SELECT id FROM permissions WHERE code = 'ASSET_MANAGEMENT'), 3),
('Delete Asset', 'ASSET_DELETE', 'asset', 'delete', 'Delete asset', 'API', (SELECT id FROM permissions WHERE code = 'ASSET_MANAGEMENT'), 4);

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
    'USER_MANAGEMENT', 'USER_CREATE', 'USER_READ', 'USER_UPDATE',
    'ROLE_MANAGEMENT', 'ROLE_READ',
    'ASSET_MANAGEMENT', 'ASSET_CREATE', 'ASSET_READ', 'ASSET_UPDATE', 'ASSET_DELETE'
);

-- 为普通用户分配基本权限
INSERT INTO role_permissions (role_id, permission_id)
SELECT 
    (SELECT id FROM roles WHERE code = 'USER'),
    p.id
FROM permissions p
WHERE p.code IN ('ASSET_READ');

-- 插入默认用户 (密码使用BCrypt加密后的"admin123")
INSERT INTO users (username, email, password, first_name, last_name, enabled) VALUES
('admin', 'admin@company.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'System', 'Administrator', true),
('manager', 'manager@company.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Department', 'Manager', true),
('user', 'user@company.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Regular', 'User', true);

-- 分配用户角色
INSERT INTO user_roles (user_id, role_id) VALUES
((SELECT id FROM users WHERE username = 'admin'), (SELECT id FROM roles WHERE code = 'SUPER_ADMIN')),
((SELECT id FROM users WHERE username = 'manager'), (SELECT id FROM roles WHERE code = 'ADMIN')),
((SELECT id FROM users WHERE username = 'user'), (SELECT id FROM roles WHERE code = 'USER'));

-- 插入示例资产数据
INSERT INTO assets (name, asset_number, type, category, description, brand, model, status, location, department) VALUES
('MacBook Pro 13"', 'LAPTOP-001', 'Laptop', 'IT Equipment', 'Apple MacBook Pro 13-inch 2023', 'Apple', 'MacBook Pro 13"', 'ACTIVE', 'Office Floor 1', 'IT Department'),
('Dell Monitor 27"', 'MONITOR-001', 'Monitor', 'IT Equipment', 'Dell 27-inch 4K Monitor', 'Dell', 'U2720Q', 'ACTIVE', 'Office Floor 1', 'IT Department'),
('Office Chair', 'FURNITURE-001', 'Furniture', 'Office Furniture', 'Ergonomic office chair', 'Herman Miller', 'Aeron', 'ACTIVE', 'Office Floor 2', 'HR Department');