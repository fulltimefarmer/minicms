-- 请假申请表
CREATE TABLE leave_applications (
    id BIGSERIAL PRIMARY KEY,
    applicant_id BIGINT NOT NULL,
    applicant_name VARCHAR(100) NOT NULL,
    department_id BIGINT,
    department_name VARCHAR(100),
    leave_type VARCHAR(50) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    days DECIMAL(5,2) NOT NULL,
    reason TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    current_approver_id BIGINT,
    current_approver_name VARCHAR(100),
    process_instance_id VARCHAR(100),
    process_definition_id VARCHAR(100),
    task_id VARCHAR(100),
    approval_remark TEXT,
    final_approval_time TIMESTAMP,
    final_approver_id BIGINT,
    final_approver_name VARCHAR(100),
    attachment_path VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    deleted BOOLEAN DEFAULT FALSE,
    
    CONSTRAINT fk_leave_applicant FOREIGN KEY (applicant_id) REFERENCES users(id),
    CONSTRAINT fk_leave_department FOREIGN KEY (department_id) REFERENCES departments(id),
    CONSTRAINT fk_leave_current_approver FOREIGN KEY (current_approver_id) REFERENCES users(id),
    CONSTRAINT fk_leave_final_approver FOREIGN KEY (final_approver_id) REFERENCES users(id)
);

-- 请假审批历史表
CREATE TABLE leave_approval_history (
    id BIGSERIAL PRIMARY KEY,
    leave_application_id BIGINT NOT NULL,
    approver_id BIGINT NOT NULL,
    approver_name VARCHAR(100) NOT NULL,
    approver_department_id BIGINT,
    approver_department_name VARCHAR(100),
    before_status VARCHAR(20),
    after_status VARCHAR(20),
    action VARCHAR(20) NOT NULL,
    comment TEXT,
    approval_time TIMESTAMP NOT NULL,
    task_id VARCHAR(100),
    task_name VARCHAR(100),
    approval_level INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    deleted BOOLEAN DEFAULT FALSE,
    
    CONSTRAINT fk_history_leave_application FOREIGN KEY (leave_application_id) REFERENCES leave_applications(id),
    CONSTRAINT fk_history_approver FOREIGN KEY (approver_id) REFERENCES users(id),
    CONSTRAINT fk_history_approver_department FOREIGN KEY (approver_department_id) REFERENCES departments(id)
);

-- 创建索引
CREATE INDEX idx_leave_applications_applicant_id ON leave_applications(applicant_id);
CREATE INDEX idx_leave_applications_department_id ON leave_applications(department_id);
CREATE INDEX idx_leave_applications_status ON leave_applications(status);
CREATE INDEX idx_leave_applications_current_approver_id ON leave_applications(current_approver_id);
CREATE INDEX idx_leave_applications_start_date ON leave_applications(start_date);
CREATE INDEX idx_leave_applications_end_date ON leave_applications(end_date);
CREATE INDEX idx_leave_applications_process_instance_id ON leave_applications(process_instance_id);
CREATE INDEX idx_leave_applications_created_at ON leave_applications(created_at);

CREATE INDEX idx_leave_approval_history_leave_application_id ON leave_approval_history(leave_application_id);
CREATE INDEX idx_leave_approval_history_approver_id ON leave_approval_history(approver_id);
CREATE INDEX idx_leave_approval_history_approval_time ON leave_approval_history(approval_time);
CREATE INDEX idx_leave_approval_history_task_id ON leave_approval_history(task_id);

-- 添加触发器更新 updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_leave_applications_updated_at BEFORE UPDATE ON leave_applications
FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_leave_approval_history_updated_at BEFORE UPDATE ON leave_approval_history
FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- 添加注释
COMMENT ON TABLE leave_applications IS '请假申请表';
COMMENT ON COLUMN leave_applications.applicant_id IS '申请人ID';
COMMENT ON COLUMN leave_applications.applicant_name IS '申请人姓名';
COMMENT ON COLUMN leave_applications.department_id IS '申请人部门ID';
COMMENT ON COLUMN leave_applications.department_name IS '申请人部门名称';
COMMENT ON COLUMN leave_applications.leave_type IS '请假类型';
COMMENT ON COLUMN leave_applications.start_date IS '请假开始日期';
COMMENT ON COLUMN leave_applications.end_date IS '请假结束日期';
COMMENT ON COLUMN leave_applications.days IS '请假天数';
COMMENT ON COLUMN leave_applications.reason IS '请假原因';
COMMENT ON COLUMN leave_applications.status IS '请假状态';
COMMENT ON COLUMN leave_applications.current_approver_id IS '当前审批人ID';
COMMENT ON COLUMN leave_applications.current_approver_name IS '当前审批人姓名';
COMMENT ON COLUMN leave_applications.process_instance_id IS '工作流程实例ID';
COMMENT ON COLUMN leave_applications.process_definition_id IS '工作流程定义ID';
COMMENT ON COLUMN leave_applications.task_id IS '当前任务ID';
COMMENT ON COLUMN leave_applications.approval_remark IS '审批备注';
COMMENT ON COLUMN leave_applications.final_approval_time IS '最终审批时间';
COMMENT ON COLUMN leave_applications.final_approver_id IS '最终审批人ID';
COMMENT ON COLUMN leave_applications.final_approver_name IS '最终审批人姓名';
COMMENT ON COLUMN leave_applications.attachment_path IS '附件路径';

COMMENT ON TABLE leave_approval_history IS '请假审批历史表';
COMMENT ON COLUMN leave_approval_history.leave_application_id IS '请假申请ID';
COMMENT ON COLUMN leave_approval_history.approver_id IS '审批人ID';
COMMENT ON COLUMN leave_approval_history.approver_name IS '审批人姓名';
COMMENT ON COLUMN leave_approval_history.approver_department_id IS '审批人部门ID';
COMMENT ON COLUMN leave_approval_history.approver_department_name IS '审批人部门名称';
COMMENT ON COLUMN leave_approval_history.before_status IS '审批前状态';
COMMENT ON COLUMN leave_approval_history.after_status IS '审批后状态';
COMMENT ON COLUMN leave_approval_history.action IS '审批动作';
COMMENT ON COLUMN leave_approval_history.comment IS '审批意见';
COMMENT ON COLUMN leave_approval_history.approval_time IS '审批时间';
COMMENT ON COLUMN leave_approval_history.task_id IS '工作流任务ID';
COMMENT ON COLUMN leave_approval_history.task_name IS '工作流任务名称';
COMMENT ON COLUMN leave_approval_history.approval_level IS '审批层级';