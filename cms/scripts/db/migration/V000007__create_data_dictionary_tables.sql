-- 创建数据字典类型表
CREATE TABLE dict_type (
    id BIGSERIAL PRIMARY KEY,
    type_code VARCHAR(50) NOT NULL UNIQUE,
    type_name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', -- 状态：ACTIVE-启用，INACTIVE-禁用
    sort_order INTEGER DEFAULT 0, -- 排序顺序
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    deleted BOOLEAN DEFAULT FALSE
);

-- 创建数据字典项表
CREATE TABLE dict_item (
    id BIGSERIAL PRIMARY KEY,
    type_id BIGINT NOT NULL, -- 字典类型ID
    item_code VARCHAR(50) NOT NULL, -- 字典项编码
    item_name VARCHAR(100) NOT NULL, -- 字典项名称
    item_value VARCHAR(500), -- 字典项值
    description VARCHAR(500), -- 描述
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', -- 状态：ACTIVE-启用，INACTIVE-禁用
    sort_order INTEGER DEFAULT 0, -- 排序顺序
    parent_id BIGINT, -- 父级字典项ID（支持层级结构）
    level_depth INTEGER DEFAULT 1, -- 层级深度
    css_class VARCHAR(100), -- CSS样式类
    icon VARCHAR(50), -- 图标
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    deleted BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_dict_item_type_id FOREIGN KEY (type_id) REFERENCES dict_type(id),
    CONSTRAINT fk_dict_item_parent_id FOREIGN KEY (parent_id) REFERENCES dict_item(id)
);

-- 创建索引
CREATE INDEX idx_dict_type_code ON dict_type(type_code);
CREATE INDEX idx_dict_type_status ON dict_type(status);
CREATE INDEX idx_dict_item_type_id ON dict_item(type_id);
CREATE INDEX idx_dict_item_code ON dict_item(item_code);
CREATE INDEX idx_dict_item_status ON dict_item(status);
CREATE INDEX idx_dict_item_parent_id ON dict_item(parent_id);

-- 插入初始数据字典类型
INSERT INTO dict_type (type_code, type_name, description, status, sort_order, created_by, updated_by) VALUES
('user_status', '用户状态', '用户账号状态字典', 'ACTIVE', 1, 'system', 'system'),
('gender', '性别', '性别字典', 'ACTIVE', 2, 'system', 'system'),
('dept_type', '部门类型', '部门类型字典', 'ACTIVE', 3, 'system', 'system'),
('asset_type', '资产类型', '资产类型字典', 'ACTIVE', 4, 'system', 'system'),
('asset_status', '资产状态', '资产状态字典', 'ACTIVE', 5, 'system', 'system'),
('doc_type', '文档类型', '文档类型字典', 'ACTIVE', 6, 'system', 'system'),
('priority_level', '优先级', '优先级别字典', 'ACTIVE', 7, 'system', 'system'),
('audit_risk_level', '风险级别', '审计风险级别字典', 'ACTIVE', 8, 'system', 'system');

-- 插入初始数据字典项
INSERT INTO dict_item (type_id, item_code, item_name, item_value, description, status, sort_order, created_by, updated_by) VALUES
-- 用户状态
((SELECT id FROM dict_type WHERE type_code = 'user_status'), 'active', '正常', 'ACTIVE', '用户账号正常状态', 'ACTIVE', 1, 'system', 'system'),
((SELECT id FROM dict_type WHERE type_code = 'user_status'), 'inactive', '禁用', 'INACTIVE', '用户账号被禁用', 'ACTIVE', 2, 'system', 'system'),
((SELECT id FROM dict_type WHERE type_code = 'user_status'), 'locked', '锁定', 'LOCKED', '用户账号被锁定', 'ACTIVE', 3, 'system', 'system'),

-- 性别
((SELECT id FROM dict_type WHERE type_code = 'gender'), 'male', '男', 'M', '男性', 'ACTIVE', 1, 'system', 'system'),
((SELECT id FROM dict_type WHERE type_code = 'gender'), 'female', '女', 'F', '女性', 'ACTIVE', 2, 'system', 'system'),
((SELECT id FROM dict_type WHERE type_code = 'gender'), 'unknown', '未知', 'U', '性别未知', 'ACTIVE', 3, 'system', 'system'),

-- 部门类型
((SELECT id FROM dict_type WHERE type_code = 'dept_type'), 'company', '公司', 'COMPANY', '公司级别', 'ACTIVE', 1, 'system', 'system'),
((SELECT id FROM dict_type WHERE type_code = 'dept_type'), 'division', '事业部', 'DIVISION', '事业部级别', 'ACTIVE', 2, 'system', 'system'),
((SELECT id FROM dict_type WHERE type_code = 'dept_type'), 'department', '部门', 'DEPARTMENT', '部门级别', 'ACTIVE', 3, 'system', 'system'),
((SELECT id FROM dict_type WHERE type_code = 'dept_type'), 'team', '小组', 'TEAM', '小组级别', 'ACTIVE', 4, 'system', 'system'),

-- 资产类型
((SELECT id FROM dict_type WHERE type_code = 'asset_type'), 'hardware', '硬件设备', 'HARDWARE', '硬件类资产', 'ACTIVE', 1, 'system', 'system'),
((SELECT id FROM dict_type WHERE type_code = 'asset_type'), 'software', '软件资产', 'SOFTWARE', '软件类资产', 'ACTIVE', 2, 'system', 'system'),
((SELECT id FROM dict_type WHERE type_code = 'asset_type'), 'license', '许可证', 'LICENSE', '许可证类资产', 'ACTIVE', 3, 'system', 'system'),

-- 资产状态
((SELECT id FROM dict_type WHERE type_code = 'asset_status'), 'available', '可用', 'AVAILABLE', '资产可用状态', 'ACTIVE', 1, 'system', 'system'),
((SELECT id FROM dict_type WHERE type_code = 'asset_status'), 'in_use', '使用中', 'IN_USE', '资产使用中', 'ACTIVE', 2, 'system', 'system'),
((SELECT id FROM dict_type WHERE type_code = 'asset_status'), 'maintenance', '维护中', 'MAINTENANCE', '资产维护中', 'ACTIVE', 3, 'system', 'system'),
((SELECT id FROM dict_type WHERE type_code = 'asset_status'), 'retired', '已报废', 'RETIRED', '资产已报废', 'ACTIVE', 4, 'system', 'system'),

-- 优先级
((SELECT id FROM dict_type WHERE type_code = 'priority_level'), 'low', '低', 'LOW', '低优先级', 'ACTIVE', 1, 'system', 'system'),
((SELECT id FROM dict_type WHERE type_code = 'priority_level'), 'medium', '中', 'MEDIUM', '中优先级', 'ACTIVE', 2, 'system', 'system'),
((SELECT id FROM dict_type WHERE type_code = 'priority_level'), 'high', '高', 'HIGH', '高优先级', 'ACTIVE', 3, 'system', 'system'),
((SELECT id FROM dict_type WHERE type_code = 'priority_level'), 'urgent', '紧急', 'URGENT', '紧急优先级', 'ACTIVE', 4, 'system', 'system'),

-- 风险级别
((SELECT id FROM dict_type WHERE type_code = 'audit_risk_level'), 'low', '低风险', 'LOW', '低风险操作', 'ACTIVE', 1, 'system', 'system'),
((SELECT id FROM dict_type WHERE type_code = 'audit_risk_level'), 'medium', '中风险', 'MEDIUM', '中风险操作', 'ACTIVE', 2, 'system', 'system'),
((SELECT id FROM dict_type WHERE type_code = 'audit_risk_level'), 'high', '高风险', 'HIGH', '高风险操作', 'ACTIVE', 3, 'system', 'system');