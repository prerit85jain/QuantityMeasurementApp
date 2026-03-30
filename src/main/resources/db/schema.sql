-- ============================================================
-- Quantity Measurement App - PostgreSQL Schema (AWS RDS)
-- ============================================================
-- NOTE: This file is only used by H2 in dev (schema.sql is
-- picked up automatically). In production, Hibernate's
-- ddl-auto=update creates/updates tables on startup.
-- ============================================================

-- ============================================================
-- Main entity table
-- ============================================================
CREATE TABLE IF NOT EXISTS quantity_measurement_entity (
    id                      BIGSERIAL       PRIMARY KEY,
    this_value              DOUBLE PRECISION NOT NULL,
    this_unit               VARCHAR(50)     NOT NULL,
    this_measurement_type   VARCHAR(50)     NOT NULL,
    that_value              DOUBLE PRECISION,
    that_unit               VARCHAR(50),
    that_measurement_type   VARCHAR(50),
    operation               VARCHAR(20)     NOT NULL,
    result_value            DOUBLE PRECISION,
    result_unit             VARCHAR(50),
    result_measurement_type VARCHAR(50),
    result_string           VARCHAR(100),
    is_error                BOOLEAN         NOT NULL DEFAULT FALSE,
    error_message           VARCHAR(500),
    created_at              TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_operation
    ON quantity_measurement_entity (operation);

CREATE INDEX IF NOT EXISTS idx_measurement_type
    ON quantity_measurement_entity (this_measurement_type);

CREATE INDEX IF NOT EXISTS idx_created_at
    ON quantity_measurement_entity (created_at);

-- ============================================================
-- Users table
-- ============================================================
CREATE TABLE IF NOT EXISTS app_users (
    id          BIGSERIAL       PRIMARY KEY,
    username    VARCHAR(100)    NOT NULL UNIQUE,
    password    VARCHAR(255),
    role        VARCHAR(50)     NOT NULL,
    provider    VARCHAR(20)     NOT NULL,
    provider_id VARCHAR(255)
);

-- ============================================================
-- History / audit table
-- ============================================================
CREATE TABLE IF NOT EXISTS quantity_measurement_history (
    id              BIGSERIAL       PRIMARY KEY,
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
