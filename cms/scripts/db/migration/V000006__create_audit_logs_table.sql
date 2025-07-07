-- 创建审计日志表
-- 用于记录所有用户操作和系统行为

CREATE TABLE audit_logs (
    id BIGSERIAL PRIMARY KEY,
    
    -- 操作基本信息
    operation_type VARCHAR(50) NOT NULL,        -- 操作类型: CREATE, UPDATE, DELETE, LOGIN, LOGOUT, QUERY等
    operation_name VARCHAR(100) NOT NULL,       -- 操作名称: 用户登录、创建用户、修改资产等
    operation_desc TEXT,                        -- 操作描述
    
    -- 请求信息
    request_method VARCHAR(10),                 -- HTTP方法: GET, POST, PUT, DELETE
    request_url VARCHAR(500),                   -- 请求URL
    request_params TEXT,                        -- 请求参数(JSON格式)
    request_body TEXT,                          -- 请求体(JSON格式)
    request_headers TEXT,                       -- 请求头(JSON格式)
    
    -- 响应信息
    response_status INTEGER,                    -- HTTP响应状态码
    response_body TEXT,                         -- 响应体(JSON格式)
    response_time BIGINT,                       -- 响应时间(毫秒)
    
    -- 业务信息
    business_module VARCHAR(50),                -- 业务模块: USER, ASSET, AUTH, SYSTEM等
    target_type VARCHAR(50),                    -- 目标对象类型: User, Asset, Role等
    target_id VARCHAR(100),                     -- 目标对象ID
    target_name VARCHAR(200),                   -- 目标对象名称
    
    -- 变更信息(用于UPDATE操作)
    old_values TEXT,                           -- 修改前的值(JSON格式)
    new_values TEXT,                           -- 修改后的值(JSON格式)
    changed_fields TEXT,                       -- 变更字段列表(JSON数组)
    
    -- 用户和环境信息
    user_id BIGINT,                            -- 操作用户ID
    username VARCHAR(100),                     -- 操作用户名
    user_real_name VARCHAR(100),               -- 操作用户真实姓名
    ip_address VARCHAR(45),                    -- 客户端IP地址
    user_agent TEXT,                           -- 用户代理信息
    session_id VARCHAR(100),                   -- 会话ID
    
    -- 系统信息
    server_name VARCHAR(100),                  -- 服务器名称
    thread_id VARCHAR(50),                     -- 线程ID
    trace_id VARCHAR(100),                     -- 链路追踪ID
    
    -- 状态和结果
    status VARCHAR(20) DEFAULT 'SUCCESS',      -- 操作状态: SUCCESS, FAILED, PARTIAL
    error_message TEXT,                        -- 错误信息
    risk_level VARCHAR(20) DEFAULT 'LOW',      -- 风险级别: LOW, MEDIUM, HIGH, CRITICAL
    
    -- 时间信息
    start_time TIMESTAMP NOT NULL,             -- 操作开始时间
    end_time TIMESTAMP,                        -- 操作结束时间
    operation_date DATE NOT NULL,              -- 操作日期(用于分区查询)
    
    -- 审计元数据
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255) DEFAULT 'system',
    deleted BOOLEAN DEFAULT FALSE
);

-- 创建索引优化查询性能
CREATE INDEX idx_audit_logs_user_id ON audit_logs(user_id);
CREATE INDEX idx_audit_logs_username ON audit_logs(username);
CREATE INDEX idx_audit_logs_operation_type ON audit_logs(operation_type);
CREATE INDEX idx_audit_logs_business_module ON audit_logs(business_module);
CREATE INDEX idx_audit_logs_target_type ON audit_logs(target_type);
CREATE INDEX idx_audit_logs_target_id ON audit_logs(target_id);
CREATE INDEX idx_audit_logs_ip_address ON audit_logs(ip_address);
CREATE INDEX idx_audit_logs_status ON audit_logs(status);
CREATE INDEX idx_audit_logs_risk_level ON audit_logs(risk_level);
CREATE INDEX idx_audit_logs_operation_date ON audit_logs(operation_date);
CREATE INDEX idx_audit_logs_start_time ON audit_logs(start_time);
CREATE INDEX idx_audit_logs_trace_id ON audit_logs(trace_id);

-- 复合索引用于常见查询场景
CREATE INDEX idx_audit_logs_user_time ON audit_logs(user_id, operation_date DESC);
CREATE INDEX idx_audit_logs_module_time ON audit_logs(business_module, operation_date DESC);
CREATE INDEX idx_audit_logs_type_time ON audit_logs(operation_type, operation_date DESC);

-- 添加表注释
COMMENT ON TABLE audit_logs IS '系统审计日志表，记录所有用户操作和系统行为';
COMMENT ON COLUMN audit_logs.operation_type IS '操作类型：CREATE,UPDATE,DELETE,LOGIN,LOGOUT,QUERY等';
COMMENT ON COLUMN audit_logs.business_module IS '业务模块：USER,ASSET,AUTH,SYSTEM等';
COMMENT ON COLUMN audit_logs.risk_level IS '风险级别：LOW,MEDIUM,HIGH,CRITICAL';
COMMENT ON COLUMN audit_logs.status IS '操作状态：SUCCESS,FAILED,PARTIAL';