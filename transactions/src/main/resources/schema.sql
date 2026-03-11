-- Global Defaults
CREATE TABLE global_categorization (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    set_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    name VARCHAR(30) NOT NULL UNIQUE,
    category VARCHAR(20) NOT NULL,
    regular_interval INT DEFAULT 0
);
CREATE INDEX idx_global_name ON global_categorization (name);

-- User Preferences
CREATE TABLE user_profile (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    merchant_name VARCHAR(30) NOT NULL,
    category VARCHAR(20) NOT NULL,
regular_interval INT DEFAULT 0
);
CREATE UNIQUE INDEX uq_user_merchant ON user_profile (user_id, merchant_name);

-- The Ledger
CREATE TABLE transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    user_id INT NOT NULL,
    name VARCHAR(30) NOT NULL,
    amount DECIMAL(19,4) NOT NULL,
    default_category VARCHAR(20),
    user_defined_category VARCHAR(20),
    default_regular_interval INT DEFAULT 0,
    user_defined_regular INT DEFAULT NULL,
    comment VARCHAR(50),
    is_on_budget BOOLEAN DEFAULT TRUE,
    system_flag INT DEFAULT 0
);
CREATE INDEX idx_txn_user ON transactions (user_id);
CREATE INDEX idx_txn_name ON transactions (name);