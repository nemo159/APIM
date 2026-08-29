ALTER TABLE members
    ADD COLUMN phone_encrypted TEXT,
    ADD COLUMN phone_hash VARCHAR(64),
    ADD COLUMN password_hash VARCHAR(100),
    ADD COLUMN password_change_required BOOLEAN NOT NULL DEFAULT TRUE,
    ADD COLUMN del_yn CHAR(1) NOT NULL DEFAULT 'Y',
    ADD COLUMN updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN created_by VARCHAR(100) NOT NULL DEFAULT 'SYSTEM',
    ADD COLUMN updated_by VARCHAR(100) NOT NULL DEFAULT 'SYSTEM';

CREATE UNIQUE INDEX uk_members_name ON members (LOWER(name));
CREATE UNIQUE INDEX uk_members_phone_hash ON members (phone_hash) WHERE phone_hash IS NOT NULL;
CREATE INDEX idx_members_active_name ON members (del_yn, name);

CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE,
    description VARCHAR(100) NOT NULL
);

INSERT INTO roles (code, description) VALUES
    ('MASTER', '최고 관리자'),
    ('ADMIN', '회원 관리자'),
    ('MEMBER', '일반 회원');

CREATE TABLE member_roles (
    member_id BIGINT NOT NULL REFERENCES members(id),
    role_id BIGINT NOT NULL REFERENCES roles(id),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL DEFAULT 'SYSTEM',
    PRIMARY KEY (member_id, role_id)
);

CREATE INDEX idx_member_roles_role_id ON member_roles (role_id);
