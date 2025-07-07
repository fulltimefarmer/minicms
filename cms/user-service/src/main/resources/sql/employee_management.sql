-- 员工信息管理相关表结构
-- 创建者: CMS系统
-- 创建时间: 2024年

-- ================================
-- 员工信息表
-- ================================
CREATE TABLE IF NOT EXISTS employees (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '关联用户ID',
    employee_number VARCHAR(50) UNIQUE NOT NULL COMMENT '工号',
    id_card VARCHAR(18) COMMENT '身份证号',
    birth_date DATE COMMENT '生日',
    gender CHAR(1) COMMENT '性别 M/F',
    marital_status VARCHAR(20) COMMENT '婚姻状况',
    education VARCHAR(50) COMMENT '学历',
    emergency_contact VARCHAR(100) COMMENT '紧急联系人',
    emergency_phone VARCHAR(20) COMMENT '紧急联系电话',
    hire_date DATE COMMENT '入职日期',
    probation_end_date DATE COMMENT '试用期结束日期',
    status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '员工状态 ACTIVE/INACTIVE/TERMINATED',
    work_location VARCHAR(200) COMMENT '工作地点',
    bank_account VARCHAR(50) COMMENT '银行账号',
    bank_name VARCHAR(100) COMMENT '开户银行',
    notes TEXT COMMENT '备注',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted BOOLEAN DEFAULT FALSE COMMENT '是否删除',
    created_by BIGINT COMMENT '创建人ID',
    updated_by BIGINT COMMENT '更新人ID',
    INDEX idx_user_id (user_id),
    INDEX idx_employee_number (employee_number),
    INDEX idx_status (status),
    INDEX idx_hire_date (hire_date),
    CONSTRAINT fk_employee_user FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='员工信息表';

-- ================================
-- 职级表
-- ================================
CREATE TABLE IF NOT EXISTS job_levels (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    name VARCHAR(100) NOT NULL COMMENT '职级名称',
    code VARCHAR(50) UNIQUE NOT NULL COMMENT '职级代码',
    description TEXT COMMENT '职级描述',
    level INT NOT NULL COMMENT '职级等级（数字越大等级越高）',
    min_salary DECIMAL(12,2) COMMENT '最低薪资',
    max_salary DECIMAL(12,2) COMMENT '最高薪资',
    responsibilities TEXT COMMENT '职责描述',
    requirements TEXT COMMENT '任职要求',
    enabled BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    sort INT DEFAULT 0 COMMENT '排序',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted BOOLEAN DEFAULT FALSE COMMENT '是否删除',
    created_by BIGINT COMMENT '创建人ID',
    updated_by BIGINT COMMENT '更新人ID',
    INDEX idx_code (code),
    INDEX idx_level (level),
    INDEX idx_enabled (enabled)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='职级表';

-- ================================
-- 薪资表
-- ================================
CREATE TABLE IF NOT EXISTS salaries (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    employee_id BIGINT NOT NULL COMMENT '员工ID',
    job_level_id BIGINT COMMENT '职级ID',
    basic_salary DECIMAL(12,2) NOT NULL DEFAULT 0.00 COMMENT '基本工资',
    performance_bonus DECIMAL(12,2) DEFAULT 0.00 COMMENT '绩效奖金',
    position_allowance DECIMAL(12,2) DEFAULT 0.00 COMMENT '岗位津贴',
    meal_allowance DECIMAL(12,2) DEFAULT 0.00 COMMENT '餐补',
    transport_allowance DECIMAL(12,2) DEFAULT 0.00 COMMENT '交通补贴',
    housing_allowance DECIMAL(12,2) DEFAULT 0.00 COMMENT '住房补贴',
    overtime_allowance DECIMAL(12,2) DEFAULT 0.00 COMMENT '加班费',
    other_allowance DECIMAL(12,2) DEFAULT 0.00 COMMENT '其他津贴',
    social_insurance DECIMAL(12,2) DEFAULT 0.00 COMMENT '社保缴费',
    housing_fund DECIMAL(12,2) DEFAULT 0.00 COMMENT '公积金缴费',
    personal_tax DECIMAL(12,2) DEFAULT 0.00 COMMENT '个人所得税',
    other_deduction DECIMAL(12,2) DEFAULT 0.00 COMMENT '其他扣款',
    gross_salary DECIMAL(12,2) DEFAULT 0.00 COMMENT '应发工资',
    net_salary DECIMAL(12,2) DEFAULT 0.00 COMMENT '实发工资',
    effective_date DATE NOT NULL COMMENT '生效日期',
    end_date DATE COMMENT '结束日期',
    status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '状态 ACTIVE/INACTIVE',
    remark TEXT COMMENT '备注',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted BOOLEAN DEFAULT FALSE COMMENT '是否删除',
    created_by BIGINT COMMENT '创建人ID',
    updated_by BIGINT COMMENT '更新人ID',
    INDEX idx_employee_id (employee_id),
    INDEX idx_job_level_id (job_level_id),
    INDEX idx_effective_date (effective_date),
    INDEX idx_status (status),
    CONSTRAINT fk_salary_employee FOREIGN KEY (employee_id) REFERENCES employees(id),
    CONSTRAINT fk_salary_job_level FOREIGN KEY (job_level_id) REFERENCES job_levels(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='薪资表';

-- ================================
-- 劳动合同表
-- ================================
CREATE TABLE IF NOT EXISTS contracts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    employee_id BIGINT NOT NULL COMMENT '员工ID',
    contract_number VARCHAR(50) UNIQUE NOT NULL COMMENT '合同编号',
    contract_type VARCHAR(20) NOT NULL COMMENT '合同类型 FIXED_TERM/INDEFINITE/TEMPORARY',
    start_date DATE NOT NULL COMMENT '合同开始日期',
    end_date DATE COMMENT '合同结束日期',
    work_content TEXT COMMENT '工作内容',
    work_location VARCHAR(200) COMMENT '工作地点',
    basic_salary DECIMAL(12,2) COMMENT '基本工资',
    working_hours VARCHAR(100) COMMENT '工作时间',
    probation_period VARCHAR(50) COMMENT '试用期',
    probation_salary DECIMAL(12,2) COMMENT '试用期工资',
    benefits TEXT COMMENT '福利待遇',
    termination_conditions TEXT COMMENT '解除条件',
    renewal_conditions TEXT COMMENT '续签条件',
    status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '合同状态 ACTIVE/EXPIRED/TERMINATED',
    file_path VARCHAR(500) COMMENT '合同文件路径',
    sign_location VARCHAR(200) COMMENT '签署地点',
    sign_date DATE COMMENT '签署日期',
    remark TEXT COMMENT '备注',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted BOOLEAN DEFAULT FALSE COMMENT '是否删除',
    created_by BIGINT COMMENT '创建人ID',
    updated_by BIGINT COMMENT '更新人ID',
    INDEX idx_employee_id (employee_id),
    INDEX idx_contract_number (contract_number),
    INDEX idx_contract_type (contract_type),
    INDEX idx_start_date (start_date),
    INDEX idx_end_date (end_date),
    INDEX idx_status (status),
    CONSTRAINT fk_contract_employee FOREIGN KEY (employee_id) REFERENCES employees(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='劳动合同表';

-- ================================
-- 联系方式表
-- ================================
CREATE TABLE IF NOT EXISTS contacts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    employee_id BIGINT NOT NULL COMMENT '员工ID',
    contact_type VARCHAR(20) NOT NULL COMMENT '联系方式类型 HOME/WORK/EMERGENCY',
    address VARCHAR(500) COMMENT '地址',
    city VARCHAR(100) COMMENT '城市',
    province VARCHAR(100) COMMENT '省份',
    postal_code VARCHAR(20) COMMENT '邮政编码',
    country VARCHAR(100) DEFAULT '中国' COMMENT '国家',
    home_phone VARCHAR(20) COMMENT '家庭电话',
    work_phone VARCHAR(20) COMMENT '工作电话',
    mobile_phone VARCHAR(20) COMMENT '手机号码',
    email VARCHAR(100) COMMENT '邮箱',
    qq VARCHAR(20) COMMENT 'QQ号',
    wechat VARCHAR(50) COMMENT '微信号',
    emergency_contact_name VARCHAR(100) COMMENT '紧急联系人姓名',
    emergency_contact_phone VARCHAR(20) COMMENT '紧急联系人电话',
    emergency_contact_relation VARCHAR(50) COMMENT '紧急联系人关系',
    is_primary BOOLEAN DEFAULT FALSE COMMENT '是否主要联系方式',
    remark TEXT COMMENT '备注',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted BOOLEAN DEFAULT FALSE COMMENT '是否删除',
    created_by BIGINT COMMENT '创建人ID',
    updated_by BIGINT COMMENT '更新人ID',
    INDEX idx_employee_id (employee_id),
    INDEX idx_contact_type (contact_type),
    INDEX idx_is_primary (is_primary),
    INDEX idx_mobile_phone (mobile_phone),
    INDEX idx_email (email),
    CONSTRAINT fk_contact_employee FOREIGN KEY (employee_id) REFERENCES employees(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='联系方式表';

-- ================================
-- 初始化职级数据
-- ================================
INSERT INTO job_levels (name, code, description, level, min_salary, max_salary, responsibilities, requirements, enabled, sort) VALUES 
('初级员工', 'JL001', '公司初级员工职级', 1, 3000.00, 5000.00, '完成分配的基础工作任务', '大专及以上学历，相关专业', TRUE, 1),
('中级员工', 'JL002', '公司中级员工职级', 2, 5000.00, 8000.00, '独立完成工作任务，协助团队合作', '本科及以上学历，1-3年工作经验', TRUE, 2),
('高级员工', 'JL003', '公司高级员工职级', 3, 8000.00, 12000.00, '带领小团队，负责重要项目', '本科及以上学历，3-5年工作经验', TRUE, 3),
('主管', 'JL004', '部门主管职级', 4, 12000.00, 18000.00, '管理团队，制定工作计划', '本科及以上学历，5年以上工作经验', TRUE, 4),
('经理', 'JL005', '部门经理职级', 5, 18000.00, 25000.00, '管理部门，制定发展策略', '本科及以上学历，具备管理经验', TRUE, 5),
('总监', 'JL006', '总监职级', 6, 25000.00, 35000.00, '负责公司重要业务线', '本科及以上学历，丰富管理经验', TRUE, 6),
('副总裁', 'JL007', '副总裁职级', 7, 35000.00, 50000.00, '协助总裁管理公司事务', '本科及以上学历，高级管理经验', TRUE, 7),
('总裁', 'JL008', '总裁职级', 8, 50000.00, 100000.00, '全面负责公司运营管理', '本科及以上学历，顶级管理经验', TRUE, 8);