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
    `created_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY(`id`)
);

CREATE TABLE `Post`(
    `id` BIGINT NOT NULL UNIQUE AUTO_INCREMENT,
    `member_id` BIGINT NOT NULL,
    `content` TEXT NULL,
    `created_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
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

CREATE TABLE `Search_History` (
    `id` BIGINT NOT NULL UNIQUE AUTO_INCREMENT,
    `member_id` BIGINT NOT NULL,
    `searched_member_id` BIGINT NOT NULL,
    `last_search_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY(`id`),
    FOREIGN KEY(`member_id`) REFERENCES Member (id),
    FOREIGN KEY(`searched_member_id`) REFERENCES member (id)
);

CREATE TABLE `Post_Comment` (
    `id` BIGINT NOT NULL UNIQUE AUTO_INCREMENT,
    `member_id` BIGINT NOT NULL,
    `post_id` BIGINT NOT NULL,
    `parent_id` BIGINT NULL,
    `content` VARCHAR(300) NOT NULL,
    `created_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted_date` DATETIME NULL,
    PRIMARY KEY(`id`),
    FOREIGN KEY(`member_id`) REFERENCES Member (id),
    FOREIGN KEY(`post_id`) REFERENCES Post (id)
);

CREATE TABLE `Post_Liker` (
    `id` BIGINT NOT NULL UNIQUE AUTO_INCREMENT,
    `member_id` BIGINT NOT NULL,
    `post_id` BIGINT NOT NULL,
    `created_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY(`id`),
    FOREIGN KEY(`member_id`) REFERENCES Member (id),
    FOREIGN KEY(`post_id`) REFERENCES Post (id)
);

CREATE TABLE `Post_Comment_Liker` (
    `id` BIGINT NOT NULL UNIQUE AUTO_INCREMENT,
    `member_id` BIGINT NOT NULL,
    `post_comment_id` BIGINT NOT NULL,
    `created_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY(`id`),
    FOREIGN KEY(`member_id`) REFERENCES Member (id),
    FOREIGN KEY(`post_comment_id`) REFERENCES Post_Comment (id)
);

CREATE TABLE `Following` (
    `id` BIGINT NOT NULL UNIQUE AUTO_INCREMENT,
    `member_id` BIGINT NOT NULL,
    `followed_member_id` BIGINT NOT NULL,
    `created_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY(`id`),
    FOREIGN KEY(`member_id`) REFERENCES Member (id),
    FOREIGN KEY(`followed_member_id`) REFERENCES Member (id)
);