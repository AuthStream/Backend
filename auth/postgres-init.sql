-- Tạo bảng applications
CREATE TABLE IF NOT EXISTS applications (
    application_id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    admin_id VARCHAR(50) NOT NULL,
    provider_id VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Tạo bảng providers
CREATE TABLE IF NOT EXISTS providers (
    provider_id VARCHAR(50) PRIMARY KEY,
    application_id VARCHAR(50),
    method_id VARCHAR(50),
    type VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Tạo bảng forward (kế thừa từ Method)
CREATE TABLE IF NOT EXISTS forward (
    method_id VARCHAR(50) PRIMARY KEY,
    application_id VARCHAR(50),
    name VARCHAR(255) NOT NULL,
    proxy_host_ip VARCHAR(255) NOT NULL,
    domain_name VARCHAR(255) NOT NULL,
    callback_url VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Tạo bảng tokens
CREATE TABLE IF NOT EXISTS tokens (
    token_id VARCHAR(50) PRIMARY KEY,
    body JSONB,
    encrypt_token VARCHAR(255) NOT NULL,
    expired_duration BIGINT NOT NULL,
    application_id VARCHAR(50) NOT NULL UNIQUE
);

-- Thêm khóa ngoại
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

-- Seed dữ liệu mẫu với 1000 bản ghi
DO $$
BEGIN
    FOR i IN 1..1000 LOOP
        -- Chèn dữ liệu vào applications
        INSERT INTO applications (application_id, name, admin_id)
        VALUES (
            'app_' || i,
            'Application ' || i,
            'admin_' || i
        );

        -- Chèn dữ liệu vào providers
        INSERT INTO providers (provider_id, application_id, type, name)
        VALUES (
            'prov_' || i,
            'app_' || i,
            CASE WHEN i % 2 = 0 THEN 'OAUTH' ELSE 'SAML' END, -- Giả định ProviderType là enum
            'Provider ' || i
        );

        -- Cập nhật provider_id trong applications
        UPDATE applications
        SET provider_id = 'prov_' || i
        WHERE application_id = 'app_' || i;

        -- Chèn dữ liệu vào forward
        INSERT INTO forward (method_id, application_id, name, proxy_host_ip, domain_name, callback_url)
        VALUES (
            'meth_' || i,
            'app_' || i,
            'Forward ' || i,
            '192.168.1.' || (i % 255),
            'domain' || i || '.com',
            'https://callback' || i || '.com'
        );

        -- Cập nhật method_id trong providers
        UPDATE providers
        SET method_id = 'meth_' || i
        WHERE provider_id = 'prov_' || i;

        -- Chèn dữ liệu vào tokens
        INSERT INTO tokens (token_id, body, encrypt_token, expired_duration, application_id)
        VALUES (
            'tok_' || i,
            '{"user_id": "user_' || i || '", "scope": "read_write"}'::jsonb,
            'enc_tok_' || i,
            3600 + (i % 7200), -- Thời gian hết hạn từ 1h đến 2h
            'app_' || i
        );
    END LOOP;
END $$;
