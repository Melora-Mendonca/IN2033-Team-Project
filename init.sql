CREATE DATABASE IF NOT EXISTS `ipos_sa`;
USE `ipos_sa`;

CREATE TABLE IF NOT EXISTS `catalogue` (
                                           `item_id` varchar(20) NOT NULL,
    `description` varchar(255) NOT NULL,
    `package_Type` varchar(255) NOT NULL,
    `unit` varchar(20) NOT NULL,
    `unit_per_pack` int NOT NULL,
    `package_cost` decimal(10,2) NOT NULL,
    `availability` int NOT NULL DEFAULT '0',
    `minimum_stock_level` int NOT NULL DEFAULT '0',
    `is_active` int DEFAULT '1',
    `buffer_percent` double DEFAULT '10.0',
    PRIMARY KEY (`item_id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `userlogin` (
                                           `user_id` int NOT NULL AUTO_INCREMENT,
                                           `username` varchar(255) NOT NULL,
    `password_hash` varchar(255) NOT NULL,
    `first_Name` varchar(255) NOT NULL,
    `sur_Name` varchar(255) NOT NULL,
    `email` varchar(255) NOT NULL,
    `role` enum('Administrator','Director of Operations','Senior Accountant','Accountant','Warehouse Employee','Delivery Employee') NOT NULL,
    `is_Active` int DEFAULT '1',
    `created_at` date DEFAULT NULL,
    `phone` varchar(20) DEFAULT NULL,
    `address` varchar(255) DEFAULT NULL,
    PRIMARY KEY (`user_id`),
    UNIQUE KEY `username` (`username`),
    UNIQUE KEY `email` (`email`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `merchant` (
                                          `merchant_id` varchar(20) NOT NULL,
    `company_name` varchar(255) NOT NULL,
    `business_type` varchar(255) DEFAULT NULL,
    `registration_number` varchar(255) DEFAULT NULL,
    `email` varchar(255) NOT NULL,
    `phone` varchar(20) DEFAULT NULL,
    `fax` varchar(20) DEFAULT NULL,
    `address` text,
    `credit_limit` decimal(10,2) DEFAULT '1000.00',
    `outstanding_balance` decimal(10,2) DEFAULT '0.00',
    `account_status` varchar(255) DEFAULT 'normal',
    `discount_type` varchar(255) DEFAULT 'fixed',
    `fixed_discount_rate` decimal(10,2) DEFAULT '0.00',
    `flexible_discount_rate` decimal(10,2) DEFAULT '0.00',
    `registration_date` date DEFAULT (curdate()),
    `is_Active` int DEFAULT '1',
    `last_payment_date` date DEFAULT NULL,
    `username` varchar(255) DEFAULT NULL,
    `password_hash` varchar(255) DEFAULT NULL,
    PRIMARY KEY (`merchant_id`),
    UNIQUE KEY `username` (`username`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `commercial_applications` (
                                                         `application_id` int NOT NULL AUTO_INCREMENT,
                                                         `company_name` varchar(255) NOT NULL,
    `registration_no` varchar(100) NOT NULL,
    `business_type` varchar(100) DEFAULT NULL,
    `director_name` varchar(100) DEFAULT NULL,
    `email` varchar(255) NOT NULL,
    `phone` varchar(20) DEFAULT NULL,
    `fax` varchar(20) DEFAULT NULL,
    `address` text,
    `application_date` date DEFAULT (curdate()),
    `status` enum('pending','approved','rejected') DEFAULT 'pending',
    `reviewed_by` int DEFAULT NULL,
    `review_date` date DEFAULT NULL,
    `review_notes` text,
    `prefer_physical_mail` int DEFAULT '0',
    PRIMARY KEY (`application_id`),
    UNIQUE KEY `registration_no` (`registration_no`),
    KEY `reviewed_by` (`reviewed_by`),
    CONSTRAINT `commercial_applications_ibfk_1` FOREIGN KEY (`reviewed_by`) REFERENCES `userlogin` (`user_id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `order` (
                                       `order_id` varchar(50) NOT NULL,
    `order_date` date NOT NULL DEFAULT (curdate()),
    `status` varchar(255) DEFAULT 'pending',
    `dispatched_by` varchar(255) DEFAULT NULL,
    `dispatched_date` date DEFAULT NULL,
    `courier_name` varchar(255) DEFAULT NULL,
    `courier_ref_no` varchar(255) DEFAULT NULL,
    `expected_delivery_date` date DEFAULT NULL,
    `total_amount` decimal(10,2) NOT NULL,
    `discount_applied` decimal(10,2) DEFAULT '0.00',
    `final_amount` decimal(10,2) NOT NULL,
    `merchant_id` varchar(20) NOT NULL,
    PRIMARY KEY (`order_id`),
    KEY `fk_order_merchant` (`merchant_id`),
    CONSTRAINT `fk_order_merchant` FOREIGN KEY (`merchant_id`) REFERENCES `merchant` (`merchant_id`) ON DELETE CASCADE ON UPDATE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `orderitem` (
                                           `order_item_id` int NOT NULL AUTO_INCREMENT,
                                           `quantity` int NOT NULL,
                                           `unit_price` decimal(10,2) NOT NULL,
    `total_price` decimal(10,2) NOT NULL,
    `order_id` varchar(50) NOT NULL,
    `catalogue_item_id` varchar(20) NOT NULL,
    PRIMARY KEY (`order_item_id`),
    KEY `catalogue_item_id` (`catalogue_item_id`),
    KEY `fk_orderitem_order` (`order_id`),
    CONSTRAINT `fk_orderitem_order` FOREIGN KEY (`order_id`) REFERENCES `order` (`order_id`) ON DELETE CASCADE ON UPDATE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `invoice` (
                                         `invoice_id` varchar(50) NOT NULL,
    `invoice_date` date NOT NULL DEFAULT (curdate()),
    `due_date` date NOT NULL,
    `total_amount` decimal(10,2) NOT NULL,
    `amount_paid` decimal(10,2) DEFAULT '0.00',
    `status` varchar(255) DEFAULT 'unpaid',
    `days_overdue` int DEFAULT '0',
    `order_id` varchar(50) NOT NULL,
    PRIMARY KEY (`invoice_id`),
    KEY `fk_invoice_order` (`order_id`),
    CONSTRAINT `fk_invoice_order` FOREIGN KEY (`order_id`) REFERENCES `order` (`order_id`) ON DELETE CASCADE ON UPDATE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `recordpayment` (
                                               `payment_id` int NOT NULL AUTO_INCREMENT,
                                               `amount` decimal(10,2) NOT NULL,
    `payment_date` date NOT NULL DEFAULT (curdate()),
    `payment_method` varchar(255) NOT NULL,
    `reference_number` varchar(255) DEFAULT NULL,
    `invoice_id` varchar(50) NOT NULL,
    `userlogin_user_id` int NOT NULL,
    PRIMARY KEY (`payment_id`),
    KEY `userlogin_user_id` (`userlogin_user_id`),
    KEY `fk_payment_invoice` (`invoice_id`),
    CONSTRAINT `fk_payment_invoice` FOREIGN KEY (`invoice_id`) REFERENCES `invoice` (`invoice_id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `recordpayment_ibfk_2` FOREIGN KEY (`userlogin_user_id`) REFERENCES `userlogin` (`user_id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `stockdelivery` (
                                               `delivery_id` int NOT NULL AUTO_INCREMENT,
                                               `quantity` int NOT NULL,
                                               `delivery_date` date NOT NULL DEFAULT (curdate()),
    `status` varchar(255) DEFAULT 'pending',
    `supplier_name` varchar(255) NOT NULL,
    `reference_number` varchar(255) DEFAULT NULL,
    `catalogue_item_id` varchar(20) NOT NULL,
    `userlogin_user_id` int NOT NULL,
    PRIMARY KEY (`delivery_id`),
    KEY `catalogue_item_id` (`catalogue_item_id`),
    KEY `userlogin_user_id` (`userlogin_user_id`),
    CONSTRAINT `stockdelivery_ibfk_1` FOREIGN KEY (`catalogue_item_id`) REFERENCES `catalogue` (`item_id`),
    CONSTRAINT `stockdelivery_ibfk_2` FOREIGN KEY (`userlogin_user_id`) REFERENCES `userlogin` (`user_id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `monthlydiscount` (
                                                 `merchant_id` varchar(20) NOT NULL,
    `discount_month` date NOT NULL,
    `total_orders` decimal(10,2) DEFAULT '0.00',
    `discount_earned` decimal(10,2) DEFAULT '0.00',
    `discount_rate` decimal(5,2) DEFAULT '0.00',
    `discount_applied` tinyint DEFAULT '0',
    `discount_paid` tinyint DEFAULT '0',
    PRIMARY KEY (`merchant_id`,`discount_month`),
    CONSTRAINT `fk_discount_merchant` FOREIGN KEY (`merchant_id`) REFERENCES `merchant` (`merchant_id`) ON DELETE CASCADE ON UPDATE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `overduepayment` (
                                                `overdue_id` int NOT NULL AUTO_INCREMENT,
                                                `amount_due` decimal(10,2) NOT NULL,
    `due_date` date NOT NULL,
    `status` varchar(50) DEFAULT 'pending',
    `days_overdue` int DEFAULT '0',
    `first_reminder_date` date DEFAULT NULL,
    `second_reminder_date` date DEFAULT NULL,
    `resolution_date` date DEFAULT NULL,
    `merchant_id` varchar(20) NOT NULL,
    `invoice_id` varchar(50) NOT NULL,
    PRIMARY KEY (`overdue_id`),
    KEY `invoice_id` (`invoice_id`),
    KEY `fk_overdue_merchant` (`merchant_id`),
    CONSTRAINT `fk_overdue_merchant` FOREIGN KEY (`merchant_id`) REFERENCES `merchant` (`merchant_id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `overduepayment_ibfk_2` FOREIGN KEY (`invoice_id`) REFERENCES `invoice` (`invoice_id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Single default admin account (password: admin123)
INSERT INTO `userlogin` (username, password_hash, first_Name, sur_Name, email, role, is_Active)
VALUES ('admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'Admin', 'User', 'admin@ipos.com', 'Administrator', 1);