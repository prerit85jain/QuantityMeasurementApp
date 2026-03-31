-- ============================================================
-- Quantity Measurement App - MySQL Schema
-- Run this manually in Railway MySQL console on first deploy
-- (Hibernate ddl-auto=update handles it automatically too)
-- ============================================================

CREATE TABLE IF NOT EXISTS app_users (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(255) NOT NULL UNIQUE,
    password    VARCHAR(255),
    role        VARCHAR(50)  NOT NULL DEFAULT 'USER',
    provider    VARCHAR(50)  NOT NULL DEFAULT 'LOCAL',
    provider_id VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS quantity_measurement_entity (
    id                      BIGINT AUTO_INCREMENT PRIMARY KEY,
    this_value              DOUBLE       NOT NULL,
    this_unit               VARCHAR(50)  NOT NULL,
    this_measurement_type   VARCHAR(50)  NOT NULL,
    that_value              DOUBLE       NOT NULL DEFAULT 0,
    that_unit               VARCHAR(50)  NOT NULL DEFAULT '',
    that_measurement_type   VARCHAR(50)  NOT NULL DEFAULT '',
    operation               VARCHAR(20)  NOT NULL,
    result_value            DOUBLE,
    result_unit             VARCHAR(50),
    result_measurement_type VARCHAR(50),
    result_string           VARCHAR(100),
    is_error                TINYINT(1)   NOT NULL DEFAULT 0,
    error_message           VARCHAR(500),
    created_at              DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_operation         (operation),
    INDEX idx_measurement_type  (this_measurement_type),
    INDEX idx_created_at        (created_at)
);
