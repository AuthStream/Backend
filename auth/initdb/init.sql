-- Kích hoạt extension uuid-ossp để tạo UUID
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Tạo bảng applications
CREATE TABLE IF NOT EXISTS applications (
    application_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL UNIQUE,
    admin_id UUID,
    provider_id UUID,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Tạo bảng providers
CREATE TABLE IF NOT EXISTS providers (
    provider_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    application_id UUID,
    method_id UUID,
    type VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Tạo bảng forward
CREATE TABLE IF NOT EXISTS forward (
    method_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    application_id UUID,
    name VARCHAR(255) NOT NULL,
    proxy_host_ip VARCHAR(255) NOT NULL,
    domain_name VARCHAR(255) NOT NULL,
    callback_url VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Tạo bảng tokens
CREATE TABLE IF NOT EXISTS tokens (
    token_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    body JSONB,
    encrypt_token VARCHAR(255) NOT NULL,
    expired_duration BIGINT NOT NULL,
    application_id UUID NOT NULL UNIQUE
);

-- Tạo bảng users (đổi user_id từ BIGSERIAL sang UUID)
CREATE TABLE IF NOT EXISTS users (
    user_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Tạo bảng groups
CREATE TABLE IF NOT EXISTS groups (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    role_id JSONB,
    descriptions VARCHAR(1000),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Tạo bảng user_group (đổi user_id từ BIGINT sang UUID)
CREATE TABLE IF NOT EXISTS user_group (
    user_id UUID NOT NULL, -- Đổi từ BIGINT sang UUID
    group_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, group_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (group_id) REFERENCES groups(id) ON DELETE CASCADE
);

-- Tạo bảng permissions
CREATE TABLE IF NOT EXISTS permissions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    api_routes JSONB,
    description VARCHAR(1000),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Tạo bảng roles
CREATE TABLE IF NOT EXISTS roles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    group_id UUID NOT NULL,
    permission_id JSONB,
    description VARCHAR(1000),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (group_id) REFERENCES groups(id) ON DELETE CASCADE
);

-- Thêm khóa ngoại cho các bảng khác
ALTER TABLE applications
    ADD CONSTRAINT fk_provider
    FOREIGN KEY (provider_id) REFERENCES providers(provider_id);

ALTER TABLE providers
    ADD CONSTRAINT fk_application_providers
    FOREIGN KEY (application_id) REFERENCES applications(application_id);

ALTER TABLE providers
    ADD CONSTRAINT fk_method
    FOREIGN KEY (method_id) REFERENCES forward(method_id);

ALTER TABLE forward
    ADD CONSTRAINT fk_application_forward
    FOREIGN KEY (application_id) REFERENCES applications(application_id);

ALTER TABLE tokens
    ADD CONSTRAINT fk_application_tokens
    FOREIGN KEY (application_id) REFERENCES applications(application_id);

-- Seed dữ liệu mẫu với 1000 bản ghi cho tất cả bảng
DO $$
DECLARE
    app_id UUID;
    prov_id UUID;
    meth_id UUID;
    new_user_id UUID; -- Đổi từ BIGINT sang UUID
    group_id UUID;
    perm_id UUID;
    new_role_id UUID;
BEGIN
    FOR i IN 1..1000 LOOP
        -- Chèn dữ liệu vào applications và lưu UUID
        INSERT INTO applications (name, admin_id)
        VALUES (
            'Application ' || i,
            'admin_' || i
        )
        RETURNING application_id INTO app_id;

        -- Chèn dữ liệu vào providers và lưu UUID
        INSERT INTO providers (application_id, type, name)
        VALUES (
            app_id,
            CASE WHEN i % 2 = 0 THEN 'OAUTH' ELSE 'FORWARD' END,
            'Provider ' || i
        )
        RETURNING provider_id INTO prov_id;

        -- Cập nhật provider_id trong applications
        UPDATE applications
        SET provider_id = prov_id
        WHERE application_id = app_id;

        -- Chèn dữ liệu vào forward và lưu UUID
        INSERT INTO forward (application_id, name, proxy_host_ip, domain_name, callback_url)
        VALUES (
            app_id,
            'Forward ' || i,
            '192.168.1.' || (i % 255),
            'domain' || i || '.com',
            'https://callback' || i || '.com'
        )
        RETURNING method_id INTO meth_id;

        -- Cập nhật method_id trong providers
        UPDATE providers
        SET method_id = meth_id
        WHERE provider_id = prov_id;

        -- Chèn dữ liệu vào tokens
        INSERT INTO tokens (body, encrypt_token, expired_duration, application_id)
        VALUES (
            jsonb_build_object('user_id', 'user_' || i, 'scope', 'read_write'),
            'enc_tok_' || i,
            3600 + (i % 7200),
            app_id
        );

        -- Chèn dữ liệu vào users và lưu user_id
        INSERT INTO users (username, password)
        VALUES (
            'user_' || i,
            'pass_' || i
        )
        RETURNING user_id INTO new_user_id;

        -- Chèn dữ liệu vào groups và lưu UUID
        INSERT INTO groups (name, role_id, descriptions)
        VALUES (
            'Group ' || i,
            jsonb_build_array('role_' || i),
            'Description for group ' || i
        )
        RETURNING id INTO group_id;

        -- Chèn dữ liệu vào user_group
        INSERT INTO user_group (user_id, group_id)
        VALUES (
            new_user_id,
            group_id
        );

        -- Chèn dữ liệu vào permissions và lưu UUID
        INSERT INTO permissions (name, api_routes, description)
        VALUES (
            'Permission ' || i,
            jsonb_build_array(jsonb_build_object('path', '/api/resource_' || i, 'method', 'GET')),
            'Description for permission ' || i
        )
        RETURNING id INTO perm_id;

        -- Chèn dữ liệu vào roles và lưu UUID
        INSERT INTO roles (name, group_id, permission_id, description)
        VALUES (
            'Role ' || i,
            group_id,
            jsonb_build_array(perm_id::text),
            'Description for role ' || i
        )
        RETURNING id INTO new_role_id;

        -- Cập nhật role_id trong groups
        UPDATE groups
        SET role_id = jsonb_build_array(new_role_id::text)
        WHERE id = group_id;
    END LOOP;
END $$;