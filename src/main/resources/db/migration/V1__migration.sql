-- V1__create_users_table.sql

-- Main users table
CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       username VARCHAR(255) NOT NULL UNIQUE,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL
);

-- Roles collection table
CREATE TABLE user_roles (
                            user_id BIGINT NOT NULL,
                            roles VARCHAR(50) NOT NULL,
                            CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id)
                                REFERENCES users (id) ON DELETE CASCADE,
                            PRIMARY KEY (user_id, roles)
);

-- Refresh tokens collection table
CREATE TABLE user_refresh_tokens (
                                     user_id BIGINT NOT NULL,
                                     refresh_tokens VARCHAR(512) NOT NULL,
                                     CONSTRAINT fk_user_refresh_tokens_user FOREIGN KEY (user_id)
                                         REFERENCES users (id) ON DELETE CASCADE,
                                     PRIMARY KEY (user_id, refresh_tokens)
);
