CREATE DATABASE IF NOT EXISTS bucket_list;
USE bucket_list;

CREATE TABLE IF NOT EXISTS member (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    login_id VARCHAR(255),
    login_pwd VARCHAR(255),
    nickname VARCHAR(255),
    email VARCHAR(255)
);

