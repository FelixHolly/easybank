INSERT INTO customer (name, email, password, role) VALUES
('felix', 'felix@example.com', '{noop}password', 'USER');

-- Example with BCrypt encoded password (password is 'test123'):
-- INSERT INTO customer (name, email, password, role) VALUES
-- ('Test User', 'test@example.com', '{bcrypt}$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'USER');
