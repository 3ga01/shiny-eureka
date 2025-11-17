-- Add new columns with default values for existing rows
ALTER TABLE users
    ADD account_non_expired BOOLEAN DEFAULT TRUE,
    ADD account_non_locked BOOLEAN DEFAULT TRUE,
    ADD credentials_non_expired BOOLEAN DEFAULT TRUE,
    ADD deleted BOOLEAN DEFAULT FALSE,
    ADD enabled BOOLEAN DEFAULT TRUE,
    ADD first_name VARCHAR(255) DEFAULT '',
    ADD last_activity_at TIMESTAMPTZ,
    ADD last_login_at TIMESTAMPTZ,
    ADD last_name VARCHAR(255) DEFAULT '',
    ADD logo_url VARCHAR(255),
    ADD version BIGINT DEFAULT 0;

-- Set NOT NULL constraints after defaults are in place
ALTER TABLE users
    ALTER COLUMN account_non_expired SET NOT NULL,
    ALTER COLUMN account_non_locked SET NOT NULL,
    ALTER COLUMN credentials_non_expired SET NOT NULL,
    ALTER COLUMN deleted SET NOT NULL,
    ALTER COLUMN enabled SET NOT NULL,
    ALTER COLUMN first_name SET NOT NULL,
    ALTER COLUMN last_name SET NOT NULL;

-- User roles table adjustments
ALTER TABLE user_roles
    DROP CONSTRAINT IF EXISTS user_roles_pkey;

ALTER TABLE user_roles
    ALTER COLUMN roles TYPE VARCHAR(255) USING (roles::VARCHAR(255)),
    ALTER COLUMN roles DROP NOT NULL;

ALTER TABLE user_roles
    ADD PRIMARY KEY (user_id, roles);

-- User refresh tokens table adjustments
ALTER TABLE user_refresh_tokens
    DROP CONSTRAINT IF EXISTS user_refresh_tokens_pkey;

ALTER TABLE user_refresh_tokens
    ALTER COLUMN refresh_tokens TYPE VARCHAR(255) USING (refresh_tokens::VARCHAR(255)),
    ALTER COLUMN refresh_tokens DROP NOT NULL;

ALTER TABLE user_refresh_tokens
    ADD PRIMARY KEY (user_id, refresh_tokens);
