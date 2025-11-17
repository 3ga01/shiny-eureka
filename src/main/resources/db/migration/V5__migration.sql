-- V5__add_user_permissions.sql

CREATE TABLE user_permissions (
                                  user_id BIGINT NOT NULL,
                                  permissions VARCHAR(50) NOT NULL,
                                  CONSTRAINT fk_user_permissions_user FOREIGN KEY (user_id)
                                      REFERENCES users (id) ON DELETE CASCADE,
                                  PRIMARY KEY (user_id, permissions)
);
