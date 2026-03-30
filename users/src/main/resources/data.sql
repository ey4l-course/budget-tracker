-- 1. Insert data into addresses_table first
INSERT INTO addresses_table (state, city, house, apartment, zipcode) VALUES
('California', 'Los Angeles', 123, 4, '90001'),
('New York', 'Brooklyn', 789, 12, '11201'),
('Texas', 'Austin', 456, 0, '73301');

-- 2. Insert data into users_table
-- Note: address_id 1, 2, and 3 correspond to the IDs generated above
INSERT INTO users_table (username, password, given_name, surname, mobile, email, address_id) VALUES
('jdoe88', 'hashed_pass_1', 'John', 'Doe', '555-0101', 'john.doe@email.com', 1),
('msmith_ny', 'hashed_pass_2', 'Mary', 'Smith', '555-0202', 'm.smith@email.com', 2),
('texan_alex', 'hashed_pass_3', 'Alex', 'Rivera', '555-0303', 'alex.r@email.com', 3);

-- INSERT INTO budget_configs (user_id, category_name, category_type, standard_percent) VALUES
-- ('system', 'total_income', 'INCOME', 1)
-- ( 'system','housing', 'EXPENSE', 0.3),
-- ( 'system','vehicle', 'EXPENSE', 0.15),
-- ( 'system','groceries', 'EXPENSE',  0.12),
-- ( 'system','education', 'EXPENSE', 0.1),
-- ( 'system','leisure', 'EXPENSE', 0.08),
-- ( 'system','vacations', 'EXPENSE', 0.05)