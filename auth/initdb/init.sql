-- Kích hoạt extension uuid-ossp để tạo UUID
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Xóa bảng cũ để tránh trùng lặp
DROP TABLE IF EXISTS user_group, roles, permissions, groups, users, forward, providers, applications, tokens CASCADE;

-- Tạo bảng tokens
CREATE TABLE IF NOT EXISTS tokens (
    token_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    body JSONB,
    encrypt_token VARCHAR(255) NOT NULL,
    expired_duration BIGINT NOT NULL,
    application_id UUID UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Tạo bảng applications
CREATE TABLE IF NOT EXISTS applications (
    application_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL UNIQUE,
    admin_id UUID NOT NULL,
    provider_id UUID UNIQUE,
    token_id UUID NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (token_id) REFERENCES tokens(token_id) DEFERRABLE INITIALLY DEFERRED
);

-- Tạo bảng providers
CREATE TABLE IF NOT EXISTS providers (
    provider_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    application_id UUID UNIQUE,
    method_id UUID,
    type VARCHAR(50) NOT NULL CHECK (type IN ('SAML', 'FORWARD', 'OAUTH', 'LDAP')),
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (application_id) REFERENCES applications(application_id) DEFERRABLE INITIALLY DEFERRED
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
    FOREIGN KEY (application_id) REFERENCES applications(application_id) DEFERRABLE INITIALLY DEFERRED
);

-- Thêm khóa ngoại còn lại
ALTER TABLE applications
    ADD CONSTRAINT fk_provider
    FOREIGN KEY (provider_id) REFERENCES providers(provider_id) DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE providers
    ADD CONSTRAINT fk_method
    FOREIGN KEY (method_id) REFERENCES forward(method_id) DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE tokens
    ADD CONSTRAINT fk_application_tokens
    FOREIGN KEY (application_id) REFERENCES applications(application_id) DEFERRABLE INITIALLY DEFERRED;

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

-- Seed dữ liệu mẫu với debug
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
    -- Tắt ràng buộc khóa ngoại tạm thời
    SET CONSTRAINTS ALL DEFERRED;

    FOR i IN 1..1000 LOOP
        RAISE NOTICE 'Inserting token %', i;
        INSERT INTO tokens (body, encrypt_token, expired_duration)
        VALUES (
            jsonb_build_object('user_id', 'user_' || i, 'scope', 'read_write'),
            'enc_tok_' || i,
            3600 + (i % 7200)
        )
        RETURNING token_id INTO tok_id;

        RAISE NOTICE 'Inserting application %', i;
        INSERT INTO applications (name, admin_id, token_id)
        VALUES (
            'Application ' || i,
            uuid_generate_v4(),
            tok_id
        )
        RETURNING application_id INTO app_id;

        RAISE NOTICE 'Updating token %', i;
        UPDATE tokens
        SET application_id = app_id
        WHERE token_id = tok_id;

        RAISE NOTICE 'Inserting provider %', i;
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

        RAISE NOTICE 'Updating application %', i;
        UPDATE applications
        SET provider_id = prov_id
        WHERE application_id = app_id;

        IF (i % 4 = 1) THEN
            RAISE NOTICE 'Inserting forward %', i;
            INSERT INTO forward (application_id, name, proxy_host_ip, domain_name, callback_url)
            VALUES (
                app_id,
                'Forward ' || i,
                '192.168.1.' || (i % 255),
                'domain' || i || '.com',
                'https://callback' || i || '.com'
            )
            RETURNING method_id INTO meth_id;

            RAISE NOTICE 'Updating provider %', i;
            UPDATE providers
            SET method_id = meth_id
            WHERE provider_id = prov_id;
        END IF;

        RAISE NOTICE 'Inserting user %', i;
        INSERT INTO users (username, password)
        VALUES (
            'user_' || i,
            'pass_' || i
        )
        RETURNING user_id INTO usr_id;

        RAISE NOTICE 'Inserting group %', i;
        INSERT INTO groups (name, role_id, descriptions)
        VALUES (
            'Group ' || i,
            jsonb_build_array('role_' || i),
            'Description for group ' || i
        )
        RETURNING id INTO grp_id;

        RAISE NOTICE 'Inserting user_group %', i;
        INSERT INTO user_group (user_id, group_id)
        VALUES (
            usr_id,
            grp_id
        );

        RAISE NOTICE 'Inserting permission %', i;
        INSERT INTO permissions (name, api_routes, description)
        VALUES (
            'Permission ' || i,
            jsonb_build_array(jsonb_build_object('path', '/api/resource_' || i, 'method', 'GET')),
            'Description for permission ' || i
        )
        RETURNING id INTO perm_id;

        RAISE NOTICE 'Inserting role %', i;
      

      
    END LOOP;

    -- Bật lại ràng buộc khóa ngoại
    SET CONSTRAINTS ALL IMMEDIATE;
END $$;