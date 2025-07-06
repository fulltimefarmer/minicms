-- V000003__add_departments_table.sql
-- 添加部门表和相关字段

-- 创建部门表
CREATE TABLE departments (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(500),
    parent_id BIGINT,
    path VARCHAR(500),
    level INTEGER DEFAULT 1,
    sort INTEGER DEFAULT 0,
    manager VARCHAR(100),
    phone VARCHAR(20),
    email VARCHAR(100),
    address VARCHAR(200),
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255) DEFAULT 'system',
    updated_by VARCHAR(255) DEFAULT 'system',
    deleted BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (parent_id) REFERENCES departments(id)
);

-- 为用户表添加部门字段
ALTER TABLE users ADD COLUMN department_id BIGINT;
ALTER TABLE users ADD CONSTRAINT fk_users_department FOREIGN KEY (department_id) REFERENCES departments(id);

-- 创建索引
CREATE INDEX idx_departments_code ON departments(code);
CREATE INDEX idx_departments_parent_id ON departments(parent_id);
CREATE INDEX idx_departments_path ON departments(path);
CREATE INDEX idx_departments_level ON departments(level);
CREATE INDEX idx_departments_enabled ON departments(enabled);
CREATE INDEX idx_users_department_id ON users(department_id);

-- 插入示例部门数据
INSERT INTO departments (name, code, description, parent_id, path, level, sort, manager, enabled) VALUES
('总经理办公室', 'CEO', '公司最高管理层', NULL, '/CEO', 1, 1, '总经理', TRUE),
('技术部', 'TECH', '技术研发部门', NULL, '/TECH', 1, 2, '技术总监', TRUE),
('市场部', 'MARKET', '市场营销部门', NULL, '/MARKET', 1, 3, '市场总监', TRUE),
('人事部', 'HR', '人力资源部门', NULL, '/HR', 1, 4, '人事经理', TRUE),
('财务部', 'FINANCE', '财务管理部门', NULL, '/FINANCE', 1, 5, '财务经理', TRUE),
('前端开发组', 'FRONTEND', '前端开发小组', (SELECT id FROM departments WHERE code = 'TECH'), '/TECH/FRONTEND', 2, 1, '前端组长', TRUE),
('后端开发组', 'BACKEND', '后端开发小组', (SELECT id FROM departments WHERE code = 'TECH'), '/TECH/BACKEND', 2, 2, '后端组长', TRUE),
('测试组', 'QA', '质量保证小组', (SELECT id FROM departments WHERE code = 'TECH'), '/TECH/QA', 2, 3, '测试组长', TRUE),
('产品运营组', 'PRODUCT', '产品运营小组', (SELECT id FROM departments WHERE code = 'MARKET'), '/MARKET/PRODUCT', 2, 1, '产品经理', TRUE),
('销售组', 'SALES', '销售团队', (SELECT id FROM departments WHERE code = 'MARKET'), '/MARKET/SALES', 2, 2, '销售经理', TRUE);

-- 添加注释
COMMENT ON TABLE departments IS '部门信息表';
COMMENT ON COLUMN departments.name IS '部门名称';
COMMENT ON COLUMN departments.code IS '部门代码';
COMMENT ON COLUMN departments.description IS '部门描述';
COMMENT ON COLUMN departments.parent_id IS '父部门ID';
COMMENT ON COLUMN departments.path IS '部门路径';
COMMENT ON COLUMN departments.level IS '部门层级';
COMMENT ON COLUMN departments.sort IS '排序号';
COMMENT ON COLUMN departments.manager IS '部门负责人';
COMMENT ON COLUMN departments.phone IS '联系电话';
COMMENT ON COLUMN departments.email IS '联系邮箱';
COMMENT ON COLUMN departments.address IS '办公地址';
COMMENT ON COLUMN departments.enabled IS '是否启用';
COMMENT ON COLUMN users.department_id IS '所属部门ID';