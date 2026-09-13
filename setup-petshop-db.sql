CREATE DATABASE IF NOT EXISTS petshopdb;
USE petshopdb;

SHOW TABLES;

CREATE USER IF NOT EXISTS 'petshop'@'localhost' IDENTIFIED BY 'petshop123';
GRANT ALL PRIVILEGES ON petshopdb.* TO 'petshop'@'localhost';
FLUSH PRIVILEGES;

SELECT 'DB creada y usuario configurado correctamente.' AS status;
