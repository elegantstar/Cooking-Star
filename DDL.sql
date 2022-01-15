CREATE TABLE `Member`(
    `id` BIGINT NOT NULL UNIQUE AUTO_INCREMENT,
    `user_id` varchar(20) NOT NULL UNIQUE,
    `password` varchar(256) NOT NULL,
    `salt` varchar(16) NOT NULL,
    `name` varchar(20) NOT NULL,
    `email` varchar(100) NOT NULL UNIQUE,
    `nickname` varchar(20) NOT NULL,
    `introduction` varchar(200) NULL,
    `gender` varchar(10) NULL,
    `profile_image` varchar(300) NULL,
    `create_date` DATE NOT NULL,
    `updated_date` DATE NOT NULL,
    PRIMARY KEY(`id`)
);

CREATE TABLE `Post`(
    `id` BIGINT NOT NULL UNIQUE AUTO_INCREMENT,
    `member_id` BIGINT NOT NULL,
    `content` TEXT NULL,
    `created_date` DATE NOT NULL,
    `updated_date` DATE NOT NULL,
    `status` varchar(30) NOT NULL,
    PRIMARY KEY(`id`),
    FOREIGN KEY(`member_id`) REFERENCES Member (id)
);

CREATE TABLE `PostImage` (
    `id` BIGINT NOT NULL UNIQUE AUTO_INCREMENT,
    `post_id` BIGINT NOT NULL,
    `url` varchar(300) NOT NULL,
    `priority` int NOT NULL,
    PRIMARY KEY(`id`),
    FOREIGN KEY(`post_id`) REFERENCES Post (id)
);

CREATE TABLE `SearchHistory` (
    `id` BIGINT NOT NULL UNIQUE AUTO_INCREMENT,
    `member_id` BIGINT NOT NULL,
    `searched_user_id` varchar(20) NOT NULL,
    `last_search_date` DATE NOT NULL,
    PRIMARY KEY(`id`),
    FOREIGN KEY(`member_id`) REFERENCES Member (id)
);