-- 更新用户密码为明文保存
-- 将所有用户的密码设置为明文"1234"

-- 更新所有现有用户的密码为1234
UPDATE users SET password = '1234' WHERE deleted = FALSE;

-- 添加注释说明密码现在以明文形式存储
COMMENT ON COLUMN users.password IS '用户密码（明文存储）';