-- ============================================================
-- Quantity Measurement App - Database Schema (UC16)
-- Production Database
-- ============================================================

-- Drop tables if they exist (for clean re-initialization)
DROP TABLE IF EXISTS quantity_measurement_history;
DROP TABLE IF EXISTS quantity_measurement_entity;
DROP TABLE IF EXISTS app_users;

-- ============================================================
-- Users table for authentication (JWT + OAuth2)
-- ============================================================
CREATE TABLE IF NOT EXISTS app_users (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    username            VARCHAR(255)    NOT NULL UNIQUE,
    password            VARCHAR(255),
    role                VARCHAR(50)     NOT NULL,
    provider            VARCHAR(50)     NOT NULL,
    provider_id         VARCHAR(255)
);

-- Index for username lookup during login
CREATE INDEX IF NOT EXISTS idx_app_users_username
    ON app_users (username);

-- ============================================================
-- Main entity table for storing quantity measurement operations
-- ============================================================
CREATE TABLE IF NOT EXISTS quantity_measurement_entity (
    id                      BIGINT AUTO_INCREMENT PRIMARY KEY,
    this_value              DOUBLE          NOT NULL,
    this_unit               VARCHAR(50)     NOT NULL,
    this_measurement_type   VARCHAR(50)     NOT NULL,
    that_value              DOUBLE,
    that_unit               VARCHAR(50),
    that_measurement_type   VARCHAR(50),
    operation               VARCHAR(20)     NOT NULL,
    result_value            DOUBLE,
    result_unit             VARCHAR(50),
    result_measurement_type VARCHAR(50),
    result_string           VARCHAR(100),
    is_error                BOOLEAN         NOT NULL DEFAULT FALSE,
    error_message           VARCHAR(500),
    created_at              TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Index for querying by operation type
CREATE INDEX IF NOT EXISTS idx_operation
    ON quantity_measurement_entity (operation);

-- Index for querying by measurement type
CREATE INDEX IF NOT EXISTS idx_measurement_type
    ON quantity_measurement_entity (this_measurement_type);

-- Index for querying by created date
CREATE INDEX IF NOT EXISTS idx_created_at
    ON quantity_measurement_entity (created_at);

-- ============================================================
-- History / audit table
-- ============================================================
CREATE TABLE IF NOT EXISTS quantity_measurement_history (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    entity_id       BIGINT          NOT NULL,
    operation       VARCHAR(20)     NOT NULL,
    changed_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    changed_by      VARCHAR(100),
    change_summary  VARCHAR(500),
    FOREIGN KEY (entity_id) REFERENCES quantity_measurement_entity(id)
        ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_history_entity_id
    ON quantity_measurement_history (entity_id);