-- SQL初始化脚本 - 运行此脚本创建数据库和表

-- 创建数据库
CREATE DATABASE IF NOT EXISTS sport_team DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE sport_team;

-- 1. 创建用户表
CREATE TABLE `users` (
    `id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '唯一标识符',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码哈希',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    `stu_id` VARCHAR(20) UNIQUE COMMENT '学号',
    `phone_num` VARCHAR(20) UNIQUE COMMENT '电话',
    `level` INT DEFAULT 1 COMMENT '用户等级',
    `gender` ENUM('MALE', 'FEMALE', 'OTHER') DEFAULT 'OTHER' COMMENT '性别',
    `create_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. 创建赛事地点表
CREATE TABLE `spots` (
    `id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '唯一标识符',
    `address` TEXT NOT NULL COMMENT '详细地址',
    `status` TINYINT DEFAULT 1 COMMENT '场地状态: 1-开放, 0-维护中'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. 创建活动表
CREATE TABLE `activities` (
    `id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '唯一标识符',
    `creator_id` INT NOT NULL COMMENT '发布者ID (关联User.id)',
    `spot_id` INT NOT NULL COMMENT '赛点ID (关联Spot.id)',
    `sport_type` VARCHAR(50) NOT NULL COMMENT '运动类型',
    `title` VARCHAR(100) NOT NULL COMMENT '活动标题',
    `description` TEXT COMMENT '活动描述',
    `max_participants` INT NOT NULL COMMENT '最大参与人数',
    `min_level_required` INT DEFAULT 1 COMMENT '最低等级要求',
    `registration_ddl` DATETIME NOT NULL COMMENT '报名截止时间',
    `start_at` DATETIME NOT NULL COMMENT '活动开始时间',
    `end_at` DATETIME NOT NULL COMMENT '活动结束时间',
    `status` TINYINT DEFAULT 1 COMMENT '活动状态: 1-招募中, 2-已满人, 3-已截止, 4-已完成, 0-已取消',
    `update_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    
    -- 建立发布和赛点的外键约束
    CONSTRAINT `fk_activity_creator` FOREIGN KEY (`creator_id`) REFERENCES `users`(`id`),
    CONSTRAINT `fk_activity_spot` FOREIGN KEY (`spot_id`) REFERENCES `spots`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. 创建报名中间表 (处理 User 和 Activity 的 m:n 关系)
CREATE TABLE `registrations` (
    `id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '报名流水ID',
    `user_id` INT NOT NULL COMMENT '报名用户ID',
    `activity_id` INT NOT NULL COMMENT '活动ID',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 1-待审核, 2-已通过, 3-已驳回, 4-已取消',
    `registration_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '报名时间',
    `cancel_at` DATETIME DEFAULT NULL COMMENT '取消时间',
    
    -- 复合唯一索引：核心约束，防止同一用户重复报名同一活动
    UNIQUE KEY `uk_user_activity` (`user_id`, `activity_id`),
    -- 建立外键约束
    CONSTRAINT `fk_reg_user` FOREIGN KEY (`user_id`) REFERENCES `users`(`id`),
    CONSTRAINT `fk_reg_activity` FOREIGN KEY (`activity_id`) REFERENCES `activities`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 添加索引以提高查询性能
CREATE INDEX `idx_activity_creator` ON `activities`(`creator_id`);
CREATE INDEX `idx_activity_sport_type` ON `activities`(`sport_type`);
CREATE INDEX `idx_activity_status` ON `activities`(`status`);
CREATE INDEX `idx_registration_user` ON `registrations`(`user_id`);
CREATE INDEX `idx_registration_activity` ON `registrations`(`activity_id`);
CREATE INDEX `idx_registration_status` ON `registrations`(`status`);

-- 插入测试数据
INSERT INTO `users` (`id`, `username`, `password`, `phone_num`, `stu_id`, `level`) VALUES
(1, 'admin', '$2a$10$SlYQmyNdGzin7olVN3p5Be7DlH.PKZbv5H8KnzzVgXXbVxzy73DMm', '13800000000', '2024001', 2);

INSERT INTO `spots` (`id`, `address`, `status`) VALUES
(1, '学校体育馆', 1),
(2, '足球场', 1),
(3, '羽毛球馆', 1),
(4, '游泳池', 1),
(5, '乒乓球馆', 1),
(6, '综合馆', 1),
(7, '排球场', 1),
(8, '田径场', 1),
(9, '篮球场', 1),
(10, '其他（见活动描述）', 1);
