-- V6__add_login_tracking_columns.sql

-- Add login tracking columns to users table
ALTER TABLE users
    ADD COLUMN login_count INT DEFAULT 0,
    ADD COLUMN failed_login_attempts INT DEFAULT 0;

-- Set NOT NULL constraints after defaults are in place
ALTER TABLE users
    ALTER COLUMN login_count SET NOT NULL,
ALTER
COLUMN failed_login_attempts SET NOT NULL;

-- Optional: initialize existing rows if needed
UPDATE users
SET login_count           = 0,
    failed_login_attempts = 0;
]