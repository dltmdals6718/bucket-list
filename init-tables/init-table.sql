CREATE DATABASE IF NOT EXISTS bucket_list;
USE bucket_list;

CREATE TABLE IF NOT EXISTS member (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    login_id VARCHAR(255),
    login_pwd VARCHAR(255),
    nickname VARCHAR(255),
    email VARCHAR(255),
    signup_date DATETIME,
    provider VARCHAR(255),
    provider_id VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS profile_image (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT,
    upload_file_name VARCHAR(255),
    store_file_name VARCHAR(255),
    FOREIGN KEY (member_id) REFERENCES member(id)
);

CREATE TABLE IF NOT EXISTS poster (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT,
    created_date DATETIME,
    title VARCHAR(255),
    content TEXT,
    FOREIGN KEY (member_id) REFERENCES member(id)
);

CREATE TABLE IF NOT EXISTS poster_image (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    poster_id BIGINT,
    upload_date DATETIME,
    upload_file_name VARCHAR(255),
    store_file_name VARCHAR(255),
    FOREIGN KEY (poster_id) REFERENCES poster(id)
);