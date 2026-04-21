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
    username VARCHAR(50) NOT NULL,
    merchant_name VARCHAR(30) NOT NULL,
    category VARCHAR(20) NOT NULL,
regular_interval INT DEFAULT 0
);
CREATE UNIQUE INDEX uq_user_merchant ON user_profile (username, merchant_name);

-- The Ledger
CREATE TABLE transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    parent_id BIGINT default null,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    username VARCHAR(50) NOT NULL,
    name VARCHAR(30) NOT NULL,
    amount DECIMAL(19,4) NOT NULL,
    is_split BOOLEAN DEFAULT false,
    default_category VARCHAR(20) DEFAULT 'other',
    user_defined_category VARCHAR(20),
    default_regular_interval INT DEFAULT 0,
    user_defined_regular INT DEFAULT NULL,
    comment VARCHAR(50),
    is_expense BOOLEAN,
    system_flag INT DEFAULT 0
);
CREATE INDEX idx_txn_user ON transactions (username);
CREATE INDEX idx_txn_name ON transactions (name);

CREATE TABLE IF NOT EXISTS budget_configs (
    username VARCHAR(50) NOT NULL,
    category_name VARCHAR(30) NOT NULL,
    category_type VARCHAR(10) CHECK (category_type IN ('INCOME', 'EXPENSE')),
    is_manual BOOLEAN DEFAULT FALSE,
    amount_limit DECIMAL(19,4) DEFAULT 0.00,
    PRIMARY KEY (username, category_name)
    );

CREATE TABLE IF NOT EXISTS monthly_snapshots (
    user_id INT NOT NULL,
    category_name VARCHAR(30) NOT NULL,
    snapshot_date DATE NOT NULL,
    actual_amount DECIMAL(19,4),
    PRIMARY KEY (user_id, category_name, snapshot_date)
    );