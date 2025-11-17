-- Add auditing columns
ALTER TABLE users
    ADD COLUMN created_at TIMESTAMPTZ DEFAULT NOW() NOT NULL,
    ADD COLUMN updated_at TIMESTAMPTZ DEFAULT NOW() NOT NULL,
    ADD COLUMN created_by VARCHAR(255),
    ADD COLUMN updated_by VARCHAR(255);

-- Set default values for existing data
UPDATE users
SET
    created_by = 'SYSTEM',
    updated_by = 'SYSTEM',
    created_at = NOW(),
    updated_at = NOW();

-- Ensure auditing fields cannot be NULL going forward
ALTER TABLE users
    ALTER COLUMN created_by SET NOT NULL,
    ALTER COLUMN updated_by SET NOT NULL;
