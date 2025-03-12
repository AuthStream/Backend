-- Kích hoạt extension uuid-ossp để tạo UUID
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Tạo bảng tokens (đặt trước vì applications cần token_id bắt buộc)
CREATE TABLE IF NOT EXISTS tokens (
    token_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    body JSONB,
    encrypt_token VARCHAR(255) NOT NULL,
    expired_duration BIGINT NOT NULL,
    application_id UUID UNIQUE, -- Sẽ được cập nhật sau khi tạo application
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Tạo bảng applications
CREATE TABLE IF NOT EXISTS applications (
    application_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL UNIQUE,
    admin_id UUID NOT NULL,
    provider_id UUID UNIQUE, -- UNIQUE vì @OneToOne
    token_id UUID NOT NULL UNIQUE, -- Bắt buộc và UNIQUE vì @OneToOne
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (token_id) REFERENCES tokens(token_id)
);

-- Tạo bảng providers
CREATE TABLE IF NOT EXISTS providers (
    provider_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    application_id UUID UNIQUE, -- UNIQUE vì @OneToOne từ Application
    method_id UUID,
    type VARCHAR(50) NOT NULL CHECK (type IN ('SAML', 'FORWARD', 'OAUTH', 'LDAP')),
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (application_id) REFERENCES applications(application_id)
);

-- Tạo bảng forward
CREATE TABLE IF NOT EXISTS forward (
    method_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    application_id UUID,
    name VARCHAR(255) NOT NULL,
    proxy_host_ip VARCHAR(255) NOT NULL,
    domain_name VARCHAR(255) NOT NULL,
    callback_url VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (application_id) REFERENCES applications(application_id)
);

-- Thêm khóa ngoại còn lại
ALTER TABLE applications
    ADD CONSTRAINT fk_provider
    FOREIGN KEY (provider_id) REFERENCES providers(provider_id);

ALTER TABLE providers
    ADD CONSTRAINT fk_method
    FOREIGN KEY (method_id) REFERENCES forward(method_id);

ALTER TABLE tokens
    ADD CONSTRAINT fk_application_tokens
    FOREIGN KEY (application_id) REFERENCES applications(application_id);

-- Tạo bảng users
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

-- Tạo bảng user_group
CREATE TABLE IF NOT EXISTS user_group (
    user_id UUID NOT NULL,
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

-- Seed dữ liệu mẫu với 1000 bản ghi
DO $$
DECLARE
    app_id UUID;
    prov_id UUID;
    meth_id UUID;
    tok_id UUID;
    usr_id UUID;
    grp_id UUID;
    perm_id UUID;
    role_id UUID;
BEGIN
    FOR i IN 1..1000 LOOP
        -- Chèn dữ liệu vào tokens trước (vì applications cần token_id)
        INSERT INTO tokens (body, encrypt_token, expired_duration)
        VALUES (
            jsonb_build_object('user_id', 'user_' || i, 'scope', 'read_write'),
            'enc_tok_' || i,
            3600 + (i % 7200)
        )
        RETURNING token_id INTO tok_id;

        -- Chèn dữ liệu vào applications
        INSERT INTO applications (name, admin_id, token_id)
        VALUES (
            'Application ' || i,
            uuid_generate_v4(), -- Sinh admin_id ngẫu nhiên
            tok_id
        )
        RETURNING application_id INTO app_id;

        -- Cập nhật application_id trong tokens
        UPDATE tokens
        SET application_id = app_id
        WHERE token_id = tok_id;

        -- Chèn dữ liệu vào providers
        INSERT INTO providers (application_id, type, name)
        VALUES (
            app_id,
            CASE WHEN i % 4 = 0 THEN 'SAML' 
                 WHEN i % 4 = 1 THEN 'FORWARD' 
                 WHEN i % 4 = 2 THEN 'OAUTH' 
                 ELSE 'LDAP' END,
            'Provider ' || i
        )
        RETURNING provider_id INTO prov_id;

        -- Cập nhật provider_id trong applications
        UPDATE applications
        SET provider_id = prov_id
        WHERE application_id = app_id;

        -- Chèn dữ liệu vào forward (chỉ khi type = FORWARD)
        IF (i % 4 = 1) THEN
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
        END IF;

        -- Chèn dữ liệu vào users
        INSERT INTO users (username, password)
        VALUES (
            'user_' || i,
            'pass_' || i
        )
        RETURNING user_id INTO usr_id;

        -- Chèn dữ liệu vào groups
        INSERT INTO groups (name, role_id, descriptions)
        VALUES (
            'Group ' || i,
            jsonb_build_array('role_' || i),
            'Description for group ' || i
        )
        RETURNING id INTO grp_id;

        -- Chèn dữ liệu vào user_group
        INSERT INTO user_group (user_id, group_id)
        VALUES (
            usr_id,
            grp_id
        );

        -- Chèn dữ liệu vào permissions
        INSERT INTO permissions (name, api_routes, description)
        VALUES (
            'Permission ' || i,
            jsonb_build_array(jsonb_build_object('path', '/api/resource_' || i, 'method', 'GET')),
            'Description for permission ' || i
        )
        RETURNING id INTO perm_id;

        -- Chèn dữ liệu vào roles
        INSERT INTO roles (name, group_id, permission_id, description)
        VALUES (
            'Role ' || i,
            grp_id,
            jsonb_build_array(perm_id::text),
            'Description for role ' || i
        )
        RETURNING id INTO role_id;

        -- Cập nhật role_id trong groups
        UPDATE groups
        SET role_id = jsonb_build_array(role_id::text)
        WHERE id = grp_id;
    END LOOP;
END $$;