-- 添加资产绑定到用户的功能
-- 为assets表添加user_id字段，支持资产绑定到用户

-- 添加user_id字段到assets表
ALTER TABLE assets ADD COLUMN user_id BIGINT;

-- 添加外键约束
ALTER TABLE assets ADD CONSTRAINT fk_assets_user_id 
    FOREIGN KEY (user_id) REFERENCES users(id);

-- 添加索引以提高查询性能
CREATE INDEX idx_assets_user_id ON assets(user_id);

-- 添加注释
COMMENT ON COLUMN assets.user_id IS '绑定的用户ID，null表示未分配';