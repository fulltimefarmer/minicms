-- 用户表
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(20),
    nickname VARCHAR(50),
    avatar VARCHAR(255),
    enabled BOOLEAN DEFAULT TRUE,
    account_non_expired BOOLEAN DEFAULT TRUE,
    account_non_locked BOOLEAN DEFAULT TRUE,
    credentials_non_expired BOOLEAN DEFAULT TRUE,
    last_login_time TIMESTAMP,
    last_login_ip VARCHAR(45),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT
);

-- 角色表
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    code VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    level INTEGER DEFAULT 0,
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT
);

-- 权限表
CREATE TABLE permissions (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    code VARCHAR(100) NOT NULL UNIQUE,
    resource VARCHAR(100),
    action VARCHAR(50),
    description VARCHAR(255),
    parent_id BIGINT,
    type VARCHAR(20) DEFAULT 'API',
    path VARCHAR(255),
    sort INTEGER DEFAULT 0,
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT
);

-- 用户角色关联表
CREATE TABLE user_roles (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    UNIQUE(user_id, role_id)
);

-- 角色权限关联表
CREATE TABLE role_permissions (
    id BIGSERIAL PRIMARY KEY,
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE,
    UNIQUE(role_id, permission_id)
);

-- 插入默认数据
-- 插入默认角色
INSERT INTO roles (name, code, description, level) VALUES
('超级管理员', 'ROLE_SUPER_ADMIN', '拥有系统所有权限', 1),
('管理员', 'ROLE_ADMIN', '系统管理员', 2),
('编辑者', 'ROLE_EDITOR', '内容编辑者', 3),
('查看者', 'ROLE_VIEWER', '只读用户', 4);

-- 插入默认权限
INSERT INTO permissions (name, code, description, type) VALUES
('用户管理', 'user:manage', '用户管理权限', 'MODULE'),
('查看用户', 'user:view', '查看用户列表', 'API'),
('创建用户', 'user:create', '创建新用户', 'API'),
('编辑用户', 'user:edit', '编辑用户信息', 'API'),
('删除用户', 'user:delete', '删除用户', 'API'),
('角色管理', 'role:manage', '角色管理权限', 'MODULE'),
('查看角色', 'role:view', '查看角色列表', 'API'),
('创建角色', 'role:create', '创建新角色', 'API'),
('编辑角色', 'role:edit', '编辑角色信息', 'API'),
('删除角色', 'role:delete', '删除角色', 'API'),
('权限管理', 'permission:manage', '权限管理', 'MODULE'),
('系统设置', 'system:manage', '系统设置权限', 'MODULE');

-- 插入默认用户（密码为123456，使用BCrypt加密）
INSERT INTO users (username, password, email, nickname, enabled) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKTnl5dCjOSG2M7xhnZqKgdAcndq', 'admin@example.com', '系统管理员', TRUE),
('editor', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKTnl5dCjOSG2M7xhnZqKgdAcndq', 'editor@example.com', '编辑者', TRUE);

-- 分配角色给用户
INSERT INTO user_roles (user_id, role_id) VALUES
(1, 1), -- admin用户分配超级管理员角色
(2, 3); -- editor用户分配编辑者角色

-- 分配权限给角色
-- 超级管理员拥有所有权限
INSERT INTO role_permissions (role_id, permission_id) 
SELECT 1, id FROM permissions;

-- 编辑者拥有部分权限
INSERT INTO role_permissions (role_id, permission_id) VALUES
(3, 2), -- 查看用户
(3, 7), -- 查看角色
(3, 12); -- 系统设置查看

-- 创建索引
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX idx_user_roles_role_id ON user_roles(role_id);
CREATE INDEX idx_role_permissions_role_id ON role_permissions(role_id);
CREATE INDEX idx_role_permissions_permission_id ON role_permissions(permission_id);