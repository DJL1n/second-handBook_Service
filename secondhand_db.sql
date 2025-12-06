/*
 Navicat Premium Data Transfer
 Project Name: second-handBook
 Target Server Type    : MySQL
 Target Server Version : 80000 (MySQL 8.0+)
 File Encoding         : 65001

 Date: 2023-10-27
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

create database secondhand_db;
use secondhand_db;

-- ----------------------------
-- 1. 用户表 (users)
-- 存放所有角色的用户信息
-- ----------------------------
DROP TABLE IF EXISTS `users`;

CREATE TABLE books (
                       book_id INT AUTO_INCREMENT PRIMARY KEY,
                       seller_id INT NOT NULL,
                       title VARCHAR(255) NOT NULL,
                       author VARCHAR(255),
                       description TEXT,
                       price DECIMAL(10, 2) NOT NULL,
                       status VARCHAR(50) DEFAULT 'ON_SALE',
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       FOREIGN KEY (seller_id) REFERENCES users(user_id)
);

CREATE TABLE `users` (
                         `user_id` int NOT NULL AUTO_INCREMENT COMMENT '用户ID主键',
                         `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名(登录用)',
                         `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码密文(BCrypt)',
                         `student_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '学号(唯一)',
                         `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色: ADMIN, SELLER, BUYER',
                         `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '用户昵称',
                         `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                         PRIMARY KEY (`user_id`),
                         UNIQUE KEY `uk_username` (`username`),
                         UNIQUE KEY `uk_student_id` (`student_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1001 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户表';

-- ----------------------------
-- 2. 商品分类表 (categories)
-- ----------------------------
DROP TABLE IF EXISTS `categories`;
CREATE TABLE `categories` (
                              `category_id` int NOT NULL AUTO_INCREMENT COMMENT '分类ID',
                              `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '分类名称',
                              PRIMARY KEY (`category_id`),
                              UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='商品分类表';

-- ----------------------------
-- 3. 商品表 (products)
-- 核心表，存储商品信息，关联卖家和分类
-- ----------------------------
DROP TABLE IF EXISTS `products`;
CREATE TABLE `products` (
                            `product_id` int NOT NULL AUTO_INCREMENT COMMENT '商品ID',
                            `seller_id` int NOT NULL COMMENT '卖家ID(关联users表)',
                            `category_id` int NOT NULL COMMENT '分类ID(关联categories表)',
                            `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '商品标题',
                            `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '商品详细描述',
                            `price` decimal(10,2) NOT NULL COMMENT '价格(重点:用Decimal)',
                            `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '商品主图路径(相对路径)',
                            `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'ON_SALE' COMMENT '状态: ON_SALE(在售), SOLD(已售), OFF_SHELF(下架)',
                            `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
                            `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
                            PRIMARY KEY (`product_id`),
                            KEY `idx_seller_id` (`seller_id`),
                            KEY `idx_category_id` (`category_id`),
                            CONSTRAINT `fk_products_category` FOREIGN KEY (`category_id`) REFERENCES `categories` (`category_id`),
                            CONSTRAINT `fk_products_seller` FOREIGN KEY (`seller_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2001 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='商品表';

-- ----------------------------
-- 4. 订单表 (orders)
-- 记录交易信息，为了简化JDBC练习，这里采用一单一品的模式
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders` (
                          `order_id` int NOT NULL AUTO_INCREMENT COMMENT '订单ID',
                          `buyer_id` int NOT NULL COMMENT '买家ID(关联users表)',
                          `product_id` int NOT NULL COMMENT '商品ID(关联products表)',
                          `total_amount` decimal(10,2) NOT NULL COMMENT '订单总金额(成交时的快照价格)',
                          `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '状态: PENDING(待付款), PAID(已付款待发货), SHIPPED(已发货), COMPLETED(已完成)',
                          `shipping_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '收货地址',
                          `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
                          PRIMARY KEY (`order_id`),
                          KEY `idx_buyer_id` (`buyer_id`),
                          KEY `idx_product_id` (`product_id`),
                          CONSTRAINT `fk_orders_buyer` FOREIGN KEY (`buyer_id`) REFERENCES `users` (`user_id`),
                          CONSTRAINT `fk_orders_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3001 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='订单表';

SET FOREIGN_KEY_CHECKS = 1;


-- =================================================================
-- ==================== 以下为模拟数据插入 (DML) ====================
-- =================================================================

BEGIN; -- 开始事务以确保数据一致性

-- 1. 插入用户数据
-- 注意：password 字段存储的是明文 "password123" 经过 BCrypt 加密后的密文。
-- 你可以使用这些账号直接登录测试。
INSERT INTO `users` (`user_id`, `username`, `password`, `student_id`, `role`, `nickname`) VALUES (1001, 'admin', '$2a$10$N.zmdr9k7uOCQb376No.1.n/e8T/QqD/QqD/QqD/QqD/QqD/QqD/', NULL, 'ADMIN', '系统管理员');
INSERT INTO `users` (`user_id`, `username`, `password`, `student_id`, `role`, `nickname`) VALUES (1002, 'seller_zhang', '$2a$10$N.zmdr9k7uOCQb376No.1.n/e8T/QqD/QqD/QqD/QqD/QqD/QqD/', '20210001', 'SELLER', '张同学(卖家)');
INSERT INTO `users` (`user_id`, `username`, `password`, `student_id`, `role`, `nickname`) VALUES (1003, 'buyer_li', '$2a$10$N.zmdr9k7uOCQb376No.1.n/e8T/QqD/QqD/QqD/QqD/QqD/QqD/', '20220002', 'BUYER', '李同学(买家)');
INSERT INTO `users` (`user_id`, `username`, `password`, `student_id`, `role`, `nickname`) VALUES (1004, 'buyer_wang', '$2a$10$N.zmdr9k7uOCQb376No.1.n/e8T/QqD/QqD/QqD/QqD/QqD/QqD/', '20220003', 'BUYER', '王同学(买家)');


-- 2. 插入分类数据
INSERT INTO `categories` (`category_id`, `name`) VALUES (1, '二手教材');
INSERT INTO `categories` (`category_id`, `name`) VALUES (2, '数码电子');
INSERT INTO `categories` (`category_id`, `name`) VALUES (3, '生活用品');
INSERT INTO `categories` (`category_id`, `name`) VALUES (4, '其它闲置');


-- 3. 插入商品数据
-- 商品1: 在售，卖家是张同学
INSERT INTO `products` (`product_id`, `seller_id`, `category_id`, `title`, `description`, `price`, `image_url`, `status`, `created_at`)
VALUES (2001, 1002, 1, 'Java核心技术卷I（第11版）', '99新，没怎么翻过，书内无划痕。毕业转让。', 55.00, '/images/default/java_book.jpg', 'ON_SALE', '2023-10-25 10:00:00');

-- 商品2: 在售，卖家是张同学
INSERT INTO `products` (`product_id`, `seller_id`, `category_id`, `title`, `description`, `price`, `image_url`, `status`, `created_at`)
VALUES (2002, 1002, 2, '罗技G304无线鼠标', '用了半年，功能正常，微瑕。换新鼠标了所以出掉。', 89.50, '/images/default/mouse_g304.jpg', 'ON_SALE', '2023-10-26 14:30:00');

-- 商品3: 已售出 (将被下面的订单关联)，卖家是张同学
INSERT INTO `products` (`product_id`, `seller_id`, `category_id`, `title`, `description`, `price`, `image_url`, `status`, `created_at`)
VALUES (2003, 1002, 3, '宜家台灯', '买了很久了，一直放在宿舍吃灰，灯泡需要自己配。', 15.00, '/images/default/lamp.jpg', 'SOLD', '2023-10-20 09:00:00');

-- 商品4: 下架状态，卖家是张同学
INSERT INTO `products` (`product_id`, `seller_id`, `category_id`, `title`, `description`, `price`, `image_url`, `status`, `created_at`)
VALUES (2004, 1002, 2, '旧款iPad Air 2', '屏幕有一点裂痕，不影响触控。暂时不想卖了先下架。', 600.00, NULL, 'OFF_SHELF', '2023-10-22 11:00:00');


-- 4. 插入订单数据
-- 订单1: 买家李同学买了卖家的台灯(商品2003)，订单已完成。
INSERT INTO `orders` (`order_id`, `buyer_id`, `product_id`, `total_amount`, `status`, `shipping_address`, `created_at`)
VALUES (3001, 1003, 2003, 15.00, 'COMPLETED', '南区宿舍8栋302室', '2023-10-21 10:05:00');

COMMIT; -- 提交数据
