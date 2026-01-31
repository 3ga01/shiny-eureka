-- V7__create_verification_tokens_table.sql

CREATE TABLE verification_tokens
(
    id          BIGSERIAL PRIMARY KEY,

    token       VARCHAR(255) NOT NULL UNIQUE,

    user_id     BIGINT       NOT NULL,

    expiry_date TIMESTAMPTZ  NOT NULL,

    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_verification_tokens_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE
);

-- Optional but recommended index for faster lookups by token
CREATE INDEX idx_verification_tokens_token
    ON verification_tokens (token);

-- Optional: ensure one active token per user
-- Uncomment if business logic requires strict 1–1
-- CREATE UNIQUE INDEX uq_verification_tokens_user
--     ON verification_tokens (user_id);