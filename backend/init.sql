-- ============================================================
-- init.sql
-- Runs automatically on first MySQL container startup.
-- Creates both databases needed by the microservices.
-- ============================================================

-- CREATE DATABASE IF NOT EXISTS user_management_db;
-- CREATE DATABASE IF NOT EXISTS credit_scoring_db;

-- -- Grant all privileges to root for both databases
-- GRANT ALL PRIVILEGES ON user_management_db.* TO 'root'@'%';
-- GRANT ALL PRIVILEGES ON credit_scoring_db.* TO 'root'@'%';

-- FLUSH PRIVILEGES;

-- Runs automatically when MySQL container starts for the first time
CREATE DATABASE IF NOT EXISTS user_management_db;
CREATE DATABASE IF NOT EXISTS credit_scoring_db;
GRANT ALL PRIVILEGES ON user_management_db.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON credit_scoring_db.* TO 'root'@'%';
FLUSH PRIVILEGES;

