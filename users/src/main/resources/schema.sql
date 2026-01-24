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
    given_name VARCHAR(50),
    surname VARCHAR(50),
    mobile VARCHAR(15),
    email VARCHAR(100),
    address_id INT,
    FOREIGN KEY (address_id) REFERENCES addresses_table(id)
);
