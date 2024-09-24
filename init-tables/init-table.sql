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
    is_private TINYINT(1),
    is_achieve TINYINT(1),
    created_date DATETIME,
    title VARCHAR(255),
    content TEXT,
    pure_content TEXT,
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

CREATE TABLE IF NOT EXISTS tag (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS poster_tag (
    poster_id BIGINT,
    tag_id BIGINT,
    PRIMARY KEY (poster_id, tag_id),
    FOREIGN KEY (poster_id) REFERENCES poster(id),
    FOREIGN KEY (tag_id) REFERENCES tag(id)
);

CREATE TABLE IF NOT EXISTS poster_achieve (
    id BIGINT PRIMARY KEY,
    member_id BIGINT,
    content TEXT,
    FOREIGN KEY (id) REFERENCES poster(id),
    FOREIGN KEY (member_id) REFERENCES member(id)
);

CREATE TABLE IF NOT EXISTS poster_achieve_image (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    poster_achieve_id BIGINT,
    upload_file_name VARCHAR(255),
    store_file_name VARCHAR(255),
    FOREIGN KEY (poster_achieve_id) REFERENCES poster_achieve(id)
);

CREATE TABLE IF NOT EXISTS poster_like (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    poster_id BIGINT,
    member_id BIGINT,
    FOREIGN KEY (poster_id) REFERENCES poster(id),
    FOREIGN KEY (member_id) REFERENCES member(id)
);

