-- Table: setting
CREATE TABLE IF NOT EXISTS setting (
    id SERIAL PRIMARY KEY,
    setting_type VARCHAR(80),
    setting_name VARCHAR(255),
    string_value VARCHAR(255),
    long_text_value TEXT,
    boolean_value BOOLEAN,
    number_value BIGINT,
    big_decimal_value NUMERIC,
    UNIQUE(setting_name)
);

-- Table: extend_setting
CREATE TABLE IF NOT EXISTS extend_setting (
    id SERIAL PRIMARY KEY,
    setting_id BIGINT,
    setting_key VARCHAR(255) NOT NULL,
    string_value VARCHAR(255),
    long_text_value TEXT,
    boolean_value BOOLEAN,
    number_value BIGINT,
    big_decimal_value NUMERIC,
    UNIQUE(setting_id, setting_key),
    FOREIGN KEY (setting_id) REFERENCES setting(id) ON DELETE CASCADE
);

-- Table: auth.account
CREATE TABLE IF NOT EXISTS auth.account (
    id SERIAL PRIMARY KEY,
    is_delete BOOLEAN DEFAULT FALSE,
    tenant_id INTEGER NOT NULL,
    account_type VARCHAR(50),
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255),
    email VARCHAR(255),
    phone VARCHAR(255),
    display_name VARCHAR(255),
    remark VARCHAR(255),
    created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(tenant_id, username)
);

-- Table: auth.role
CREATE TABLE IF NOT EXISTS auth.role (
    id SERIAL PRIMARY KEY,
    tenant_id INTEGER NOT NULL,
    role_name VARCHAR(255) NOT NULL,
    created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(tenant_id, role_name)
);

-- Table: auth.permission
CREATE TABLE IF NOT EXISTS auth.permission (
    id SERIAL PRIMARY KEY,
    tenant_id INTEGER NOT NULL,
    permission_type VARCHAR(50) NOT NULL,
    permission_name VARCHAR(255) NOT NULL,
    http_method VARCHAR(255),
    uri_pattern VARCHAR(255),
    created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(tenant_id, permission_name)
);

-- Table: auth.mapping_role_account
CREATE TABLE IF NOT EXISTS auth.mapping_role_account (
    role_id BIGINT,
    account_id BIGINT,
    PRIMARY KEY (role_id, account_id),
    FOREIGN KEY (role_id) REFERENCES auth.role(id) ON DELETE CASCADE,
    FOREIGN KEY (account_id) REFERENCES auth.account(id) ON DELETE CASCADE
);

-- Table: auth.mapping_role_permission
CREATE TABLE IF NOT EXISTS auth.mapping_role_permission (
    role_id BIGINT,
    permission_id BIGINT,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES auth.role(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES auth.permission(id) ON DELETE CASCADE
);