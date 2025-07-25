SET NAMES utf8mb4;

-- ----------------------------
-- 2.Users
-- ----------------------------
DROP TABLE if EXISTS sys_users;
CREATE TABLE sys_users (
   id BIGSERIAL PRIMARY KEY,
   username VARCHAR(50) NOT NULL UNIQUE,
   password VARCHAR(255) NOT NULL,
   email VARCHAR(100) NOT NULL UNIQUE,
   first_name VARCHAR(50),
   last_name VARCHAR(50),
   nickname VARCHAR(50),
   phone VARCHAR(20),
   avatar VARCHAR(500),
   status INTEGER DEFAULT 0,
   last_login_time TIMESTAMP,
   last_login_ip VARCHAR(45),
   failed_login_attempts INTEGER DEFAULT 0,
   locked_until TIMESTAMP,
   created_at TIMESTAMP NOT NULL DEFAULT NOW(),
   updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
   created_by VARCHAR(255) DEFAULT 'system',
   updated_by VARCHAR(255) DEFAULT 'system',
   deleted BOOLEAN DEFAULT FALSE
);

-- ----------------------------
-- 2.Roles
-- ----------------------------
DROP TABLE if EXISTS sys_roles;
CREATE TABLE sys_roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(500),
    sequence INTEGER DEFAULT 0,
    level INTEGER DEFAULT 0,
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255) DEFAULT 'system',
    updated_by VARCHAR(255) DEFAULT 'system',
    deleted BOOLEAN DEFAULT FALSE
);

-- ----------------------------
-- 3.Permissions
-- ----------------------------
drop TABLE if exists sys_permissions;
CREATE TABLE sys_permissions (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(100) NOT NULL UNIQUE,
    resource VARCHAR(100),
    action VARCHAR(50),
    description VARCHAR(500),
    parent_id BIGINT,
    type VARCHAR(20) DEFAULT 'API',
    path VARCHAR(500),
    sort INTEGER DEFAULT 0,
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255) DEFAULT 'system',
    updated_by VARCHAR(255) DEFAULT 'system',
    deleted BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (parent_id) REFERENCES permissions(id)
);

-- 角色权限关联表
CREATE TABLE role_permissions (
    id BIGSERIAL PRIMARY KEY,
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255) DEFAULT 'system',
    updated_by VARCHAR(255) DEFAULT 'system',
    deleted BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (role_id) REFERENCES roles(id),
    FOREIGN KEY (permission_id) REFERENCES permissions(id),
    UNIQUE(role_id, permission_id)
);



-- 用户角色关联表
CREATE TABLE user_roles (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255) DEFAULT 'system',
    updated_by VARCHAR(255) DEFAULT 'system',
    deleted BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (role_id) REFERENCES roles(id),
    UNIQUE(user_id, role_id)
);

-- 资产表
CREATE TABLE assets (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    asset_number VARCHAR(100) NOT NULL UNIQUE,
    type VARCHAR(50),
    category VARCHAR(50),
    description TEXT,
    brand VARCHAR(100),
    model VARCHAR(100),
    serial_number VARCHAR(100),
    purchase_price DECIMAL(15,2),
    purchase_date DATE,
    supplier VARCHAR(200),
    location VARCHAR(200),
    department VARCHAR(100),
    responsible_person VARCHAR(100),
    status VARCHAR(20) DEFAULT 'ACTIVE', -- ACTIVE, MAINTENANCE, DEPRECATED, DISPOSED
    warranty_expiry DATE,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255) DEFAULT 'system',
    updated_by VARCHAR(255) DEFAULT 'system',
    deleted BOOLEAN DEFAULT FALSE
);

-- 创建索引
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX idx_user_roles_role_id ON user_roles(role_id);
CREATE INDEX idx_role_permissions_role_id ON role_permissions(role_id);
CREATE INDEX idx_role_permissions_permission_id ON role_permissions(permission_id);
CREATE INDEX idx_permissions_parent_id ON permissions(parent_id);
CREATE INDEX idx_permissions_type ON permissions(type);
CREATE INDEX idx_permissions_code ON permissions(code);
CREATE INDEX idx_assets_asset_number ON assets(asset_number);
CREATE INDEX idx_assets_status ON assets(status);
CREATE INDEX idx_assets_type ON assets(type);
CREATE INDEX idx_assets_department ON assets(department);