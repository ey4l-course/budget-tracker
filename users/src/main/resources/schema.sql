CREATE TABLE addresses_table (
    id INT AUTO_INCREMENT PRIMARY KEY,
    state VARCHAR(50),
    city VARCHAR(50),
    house INT,
    apartment INT,
    zipcode VARCHAR(10)
);

CREATE TABLE users_table (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    is_admin BOOLEAN DEFAULT false,
    is_activated BOOLEAN DEFAULT false,
    given_name VARCHAR(50),
    surname VARCHAR(50),
    mobile VARCHAR(15),
    email VARCHAR(100),
    address_id INT,
    FOREIGN KEY (address_id) REFERENCES addresses_table(id)
);

CREATE TABLE budget_configs (
    username VARCHAR(50) NOT NULL,
    category_name VARCHAR(30) NOT NULL,
    category_type VARCHAR(10) CHECK (category_type IN ('INCOME', 'EXPENSE')),
    is_manual BOOLEAN DEFAULT FALSE,
    amount_limit DECIMAL(19,4) DEFAULT 0.00,
    PRIMARY KEY (username, category_name)
);

CREATE TABLE monthly_snapshots (
    user_id INT NOT NULL,
    category_name VARCHAR(30) NOT NULL,
    snapshot_date DATE NOT NULL,
    actual_amount DECIMAL(19,4),
    PRIMARY KEY (user_id, category_name, snapshot_date)
);