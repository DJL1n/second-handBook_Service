/*
   终极融合版数据库脚本
   包含：Users, Books(旧逻辑), Products/Orders(新逻辑)
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0; -- 临时禁用外键检查，防止删除表时报错

-- ==========================================
-- 1. 清理旧表
-- ==========================================
DROP TABLE IF EXISTS `orders`;
DROP TABLE IF EXISTS `products`;
DROP TABLE IF EXISTS `categories`;
DROP TABLE IF EXISTS `books`;
DROP TABLE IF EXISTS `users`;

-- ==========================================
-- 2. 创建表结构
-- ==========================================

-- 2.1 用户表 (Users) - 核心表
CREATE TABLE `users` (
                         `user_id` int NOT NULL AUTO_INCREMENT COMMENT '用户ID',
                         `username` varchar(50) NOT NULL COMMENT '用户名',
                         `password` varchar(100) NOT NULL COMMENT '密码(BCrypt)',
                         `student_id` varchar(20) DEFAULT NULL COMMENT '学号',
                         `role` varchar(20) NOT NULL COMMENT '角色: ADMIN, SELLER, BUYER',
                         `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
                         `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
                         PRIMARY KEY (`user_id`),
                         UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- 2.2 书籍表 (Books) - 对应目前的 Java 代码 BookService
CREATE TABLE `books` (
                         `book_id` INT AUTO_INCREMENT PRIMARY KEY,
                         `seller_id` INT NOT NULL,
                         `title` VARCHAR(255) NOT NULL,
                         `author` VARCHAR(255),
                         `description` TEXT,
                         `price` DECIMAL(10, 2) NOT NULL,
                         `status` VARCHAR(50) DEFAULT 'ON_SALE',
                         `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         FOREIGN KEY (`seller_id`) REFERENCES `users`(`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2.3 分类表 (Categories) - 扩展用
CREATE TABLE `categories` (
                              `category_id` int NOT NULL AUTO_INCREMENT,
                              `name` varchar(50) NOT NULL,
                              PRIMARY KEY (`category_id`),
                              UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- 2.4 商品表 (Products) - 扩展用
CREATE TABLE `products` (
                            `product_id` int NOT NULL AUTO_INCREMENT,
                            `seller_id` int NOT NULL,
                            `category_id` int NOT NULL,
                            `title` varchar(100) NOT NULL,
                            `description` text,
                            `price` decimal(10,2) NOT NULL,
                            `image_url` varchar(255) DEFAULT NULL,
                            `status` varchar(20) NOT NULL DEFAULT 'ON_SALE',
                            `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
                            PRIMARY KEY (`product_id`),
                            CONSTRAINT `fk_products_seller` FOREIGN KEY (`seller_id`) REFERENCES `users` (`user_id`),
                            CONSTRAINT `fk_products_category` FOREIGN KEY (`category_id`) REFERENCES `categories` (`category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2001 DEFAULT CHARSET=utf8mb4;

-- 2.5 订单表 (Orders) - 扩展用
CREATE TABLE `orders` (
                          `order_id` int NOT NULL AUTO_INCREMENT,
                          `buyer_id` int NOT NULL,
                          `product_id` int NOT NULL,
                          `total_amount` decimal(10,2) NOT NULL,
                          `status` varchar(20) NOT NULL,
                          `shipping_address` varchar(255) NOT NULL,
                          `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
                          PRIMARY KEY (`order_id`),
                          CONSTRAINT `fk_orders_buyer` FOREIGN KEY (`buyer_id`) REFERENCES `users` (`user_id`),
                          CONSTRAINT `fk_orders_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3001 DEFAULT CHARSET=utf8mb4;

-- ==========================================
-- 3. 插入数据
-- ==========================================

BEGIN;

-- 3.1 插入用户 (保留 Zhang San + 新脚本的用户)
-- 密码统一说明：
-- zhangsan 的密码是 123456
-- 其他用户的密码是 password123
INSERT INTO `users` (`user_id`, `username`, `password`, `student_id`, `role`, `nickname`) VALUES
                                                                                              (1, 'zhangsan', '$2a$10$N.zmdr9k7uOCQb376ye.5.ZncCr4ynymglffr8/J.M9tf.Uo.8aTO', '2021001', 'BUYER', '法外狂徒'),
                                                                                              (2, 'lisi', '$2a$10$N.zmdr9k7uOCQb376ye.5.ZncCr4ynymglffr8/J.M9tf.Uo.8aTO', NULL, 'SELLER', '李四-技术牛'),
                                                                                              (1001, 'admin', '$2a$10$N.zmdr9k7uOCQb376No.1.n/e8T/QqD/QqD/QqD/QqD/QqD/QqD/', NULL, 'ADMIN', '系统管理员'),
                                                                                              (1002, 'seller_zhang', '$2a$10$N.zmdr9k7uOCQb376No.1.n/e8T/QqD/QqD/QqD/QqD/QqD/QqD/', '20210001', 'SELLER', '张同学(卖家)'),
                                                                                              (1003, 'buyer_li', '$2a$10$N.zmdr9k7uOCQb376No.1.n/e8T/QqD/QqD/QqD/QqD/QqD/QqD/', '20220002', 'BUYER', '李同学(买家)');

-- 3.2 插入目前的书籍数据 (Books) - 让你的 Java 代码能查到数据
INSERT INTO `books` (`seller_id`, `title`, `author`, `description`, `price`, `status`, `created_at`) VALUES
                                                                                                         (2, 'Java核心技术 卷I', 'Cay S. Horstmann', '基础知识必备', 45.00, 'ON_SALE', NOW()),
                                                                                                         (2, 'Effective Java', 'Joshua Bloch', '进阶神书', 55.00, 'ON_SALE', NOW()),
                                                                                                         (1002, '深入理解计算机系统', 'Randal', '黑皮书', 88.00, 'ON_SALE', NOW()),
                                                                                                         (1, '二手教材', 'Unknown', '随便卖卖', 10.00, 'ON_SALE', NOW());

-- 3.3 插入分类 (Categories)
INSERT INTO `categories` (`category_id`, `name`) VALUES (1, '二手教材'), (2, '数码电子'), (3, '生活用品'), (4, '其它闲置');

-- 3.4 插入商品 (Products) - 扩展数据
INSERT INTO `products` (`product_id`, `seller_id`, `category_id`, `title`, `description`, `price`, `image_url`, `status`, `created_at`) VALUES
                                                                                                                                            (2001, 1002, 1, 'Java核心技术卷I（第11版）', '99新', 55.00, '/images/java.jpg', 'ON_SALE', NOW()),
                                                                                                                                            (2002, 1002, 2, '罗技G304无线鼠标', '微瑕', 89.50, '/images/mouse.jpg', 'ON_SALE', NOW()),
                                                                                                                                            (2003, 1002, 3, '宜家台灯', '吃灰中', 15.00, '/images/lamp.jpg', 'SOLD', NOW());

-- 3.5 插入订单 (Orders)
INSERT INTO `orders` (`order_id`, `buyer_id`, `product_id`, `total_amount`, `status`, `shipping_address`, `created_at`) VALUES
    (3001, 1003, 2003, 15.00, 'COMPLETED', '南区宿舍8栋302室', NOW());

COMMIT;

SET FOREIGN_KEY_CHECKS = 1; -- 恢复外键检查
