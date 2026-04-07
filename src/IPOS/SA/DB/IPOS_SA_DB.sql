-- MySQL dump 10.13  Distrib 8.0.45, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: ipos_sa
-- ------------------------------------------------------
-- Server version	8.4.2

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `catalogue`
--

DROP TABLE IF EXISTS `catalogue`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `catalogue` (
  `item_id` varchar(20) NOT NULL,
  `description` varchar(255) NOT NULL,
  `package_Type` varchar(255) NOT NULL,
  `unit` varchar(20) NOT NULL,
  `unit_per_pack` int NOT NULL,
  `package_cost` decimal(10,2) NOT NULL,
  `availability` int NOT NULL DEFAULT '0',
  `minimum_stock_level` int NOT NULL DEFAULT '0',
  `is_active` int DEFAULT '1',
  PRIMARY KEY (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `catalogue`
--

LOCK TABLES `catalogue` WRITE;
/*!40000 ALTER TABLE `catalogue` DISABLE KEYS */;
INSERT INTO `catalogue` VALUES ('100 00001','Paracetamol','box','Caps',200,0.10,73,300,1),('100 00002','Aspirin','box','Caps',200,0.50,12433,500,1),('100 00003','Analgin','box','Caps',10,1.20,4225,200,1),('100 00004','Celebrex, caps 100 mg','box','Caps',10,10.00,3420,200,1),('100 00005','Celebrex, caps 200 mg','box','Caps',10,18.50,1450,150,1),('100 00006','Retin-A Tretin, 30 g','box','Caps',20,25.00,2013,200,1),('100 00007','Lipitor TB, 20 mg','box','Caps',30,15.50,1562,200,1),('100 00008','Claritin CR, 60g','box','Caps',20,19.50,2540,200,1),('100 20002','Ibuprofen','Bottle','Caps',30,10.00,320,200,0),('200 00004','Iodine tincture','bottle','ml',1000,0.30,2213,200,1),('200 00005','Rhynol','bottle','ml',200,2.50,1908,300,1),('300 00001','Ospen','box','Caps',20,10.50,809,200,1),('300 00002','Ampolen','box','Caps',30,15.00,1340,300,1),('3p14','dfaldkfjlfa','dafladfhad','dfhaldifa',30,20.00,320,250,1),('400 00001','Vitamin C','box','Caps',30,1.20,3258,300,1),('400 00002','Vitamin B12','box','Caps',30,1.30,2673,300,1),('4242','fjsdjfs;dk','dladkfnadk','sdsld',30,10.00,300,250,0);
/*!40000 ALTER TABLE `catalogue` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `commercial_applications`
--

DROP TABLE IF EXISTS `commercial_applications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `commercial_applications` (
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
  PRIMARY KEY (`application_id`),
  UNIQUE KEY `registration_no` (`registration_no`),
  KEY `reviewed_by` (`reviewed_by`),
  CONSTRAINT `commercial_applications_ibfk_1` FOREIGN KEY (`reviewed_by`) REFERENCES `userlogin` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `commercial_applications`
--

LOCK TABLES `commercial_applications` WRITE;
/*!40000 ALTER TABLE `commercial_applications` DISABLE KEYS */;
INSERT INTO `commercial_applications` VALUES (1,'MedSupply Ltd','REG001','Pharmacy','John Smith','john@medsupply.com','020 1234 5678',NULL,'12 High Street, London','2026-04-03','approved',1,'2026-04-06',NULL),(2,'HealthCare Plus','REG002','Medical Supply','Jane Doe','jane@healthcare.com','020 9876 5432',NULL,'45 Park Lane, Manchester','2026-04-03','approved',1,'2026-04-03',NULL),(3,'PharmaDirect','REG003','Pharmacy','Bob Johnson','bob@pharmadirect.com','020 5555 1234',NULL,'78 Queen Street, Birmingham','2026-04-03','approved',1,'2026-04-06',NULL),(4,'CityMeds','REG004','Retail Pharmacy','Sarah Wilson','sarah@citymeds.com','020 7777 8888',NULL,'23 King Road, Leeds','2026-04-03','pending',NULL,NULL,NULL);
/*!40000 ALTER TABLE `commercial_applications` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `invoice`
--

DROP TABLE IF EXISTS `invoice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `invoice` (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `invoice`
--

LOCK TABLES `invoice` WRITE;
/*!40000 ALTER TABLE `invoice` DISABLE KEYS */;
INSERT INTO `invoice` VALUES ('INV-27240101','2026-04-06','2026-05-06',2960.00,0.00,'unpaid',0,'ORD005'),('INV-D45F8F2F','2026-04-06','2026-05-06',420.00,420.00,'paid',0,'ORD007'),('INV001','2025-03-02','2025-03-31',427.50,427.50,'paid',0,'ORD001'),('INV002','2025-03-06','2025-04-05',741.00,741.00,'paid',363,'ORD002'),('INV003','2025-03-11','2025-04-10',1250.00,0.00,'overdue',358,'ORD003'),('INV004','2025-03-13','2025-04-12',560.00,0.00,'overdue',356,'ORD004'),('INV005','2025-03-16','2025-04-15',2960.00,1000.00,'overdue',353,'ORD005'),('INV006','2025-03-19','2025-04-18',1794.50,0.00,'overdue',350,'ORD006'),('INV007','2025-03-21','2025-04-20',420.00,420.00,'paid',348,'ORD007');
/*!40000 ALTER TABLE `invoice` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `merchant`
--

DROP TABLE IF EXISTS `merchant`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `merchant` (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `merchant`
--

LOCK TABLES `merchant` WRITE;
/*!40000 ALTER TABLE `merchant` DISABLE KEYS */;
INSERT INTO `merchant` VALUES ('00019','company','business','23242543534','dfsdfhadfhksdh','1234567890','1234567890','fsufhskufhieufhead',100.00,0.00,NULL,NULL,0.00,0.00,NULL,0,'2026-04-01',NULL,NULL),('03456','ghslhs','dfjlsfjsld','djfhldfhladf','dfj;aefj;adjfc;a','1234567889','dkfalfja;kjf;af','1234567889',100.00,0.00,NULL,NULL,0.00,0.00,NULL,0,'2026-04-06',NULL,NULL),('M0001','MedSupply Ltd','Pharmacy','REG001','john@medsupply.com','020 1234 5678',NULL,'12 High Street, London',1000.00,0.00,'normal','fixed',0.00,0.00,'2026-04-06',1,NULL,NULL,NULL),('M0002','HealthCare Plus','Medical Supply','REG002','jane@healthcare.com','020 9876 5432',NULL,'45 Park Lane, Manchester',1000.00,0.00,'normal','fixed',0.00,0.00,'2026-04-03',1,NULL,NULL,NULL),('M0003','PharmaDirect','Pharmacy','REG003','bob@pharmadirect.com','020 5555 1234',NULL,'78 Queen Street, Birmingham',1000.00,0.00,'normal','fixed',0.00,0.00,'2026-04-06',1,NULL,NULL,NULL),('M001','Cosymed Ltd','Pharmacy','12345678','info@cosymed.com','0208 778 0124','0208 778 0125','3, High Level Drive, Sydenham, SE26 3ET',5000.00,89.00,'normal','fixed',5.00,0.00,'2024-01-15',1,'2025-02-28',NULL,NULL),('M002','HealthPlus Pharmacy','Pharmacy','87654321','info@healthplus.com','0207 123 4567','0207 123 4568','10 High Street, London, EC1A 1BB',3000.00,2800.00,'normal','flexible',0.00,2.00,'2024-03-20',1,'2025-01-30',NULL,NULL),('M003','MediCare Solutions','Pharmacy Chain','11223344','contact@medicare.com','0207 123 4568','0207 123 4569','22 Medical Road, Manchester, M1 1AE',10000.00,1500.00,'normal','fixed',7.50,0.00,'2024-06-10',1,'2025-03-01',NULL,NULL),('M004','Wellness Pharmacy','Pharmacy','44332211','info@wellness.com','0207 123 4569','0207 123 4570','8 Health Lane, Leeds, LS1 2BB',2000.00,2500.00,'suspended','fixed',0.00,0.00,'2024-09-05',1,'2024-12-15',NULL,NULL),('M005','CityMed Services','Pharmacy','99887766','support@citymed.com','0207 123 4570','0207 123 4571','15 Medical Park, Bristol, BS1 3CC',7500.00,800.00,'normal','flexible',0.00,3.00,'2024-11-25',1,'2025-02-20',NULL,NULL),('M006','New Pharmacy Ltd','Pharmacy','55667788','info@newpharmacy.com','0207 123 4572',NULL,'5 New Road, Birmingham, B1 1AA',1500.00,0.00,'normal','fixed',2.50,0.00,'2025-03-01',1,NULL,NULL,NULL),('M009','Boots','Pharmacy','45059305','email@email.com','123456789','123456789','1234 street',1000.00,0.00,'normal','fixed',10.00,0.00,NULL,0,'2026-03-27',NULL,NULL),('M123456','FHSLFLSHF','DF;SDJF;S','122424I2','DCNADCFL.KACN','1234456778','1234456778','FJSLDJFLSJFL.DF.KD',10.00,0.00,NULL,NULL,0.00,0.00,NULL,0,'2026-04-06',NULL,NULL);
/*!40000 ALTER TABLE `merchant` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `monthlydiscount`
--

DROP TABLE IF EXISTS `monthlydiscount`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `monthlydiscount` (
  `merchant_id` varchar(20) NOT NULL,
  `discount_month` date NOT NULL,
  `total_orders` decimal(10,2) DEFAULT '0.00',
  `discount_earned` decimal(10,2) DEFAULT '0.00',
  `discount_rate` decimal(5,2) DEFAULT '0.00',
  `discount_applied` tinyint DEFAULT '0',
  `discount_paid` tinyint DEFAULT '0',
  PRIMARY KEY (`merchant_id`,`discount_month`),
  CONSTRAINT `fk_discount_merchant` FOREIGN KEY (`merchant_id`) REFERENCES `merchant` (`merchant_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `monthlydiscount`
--

LOCK TABLES `monthlydiscount` WRITE;
/*!40000 ALTER TABLE `monthlydiscount` DISABLE KEYS */;
INSERT INTO `monthlydiscount` VALUES ('M001','2025-01-01',1250.00,62.50,5.00,1,1),('M001','2025-02-01',850.00,42.50,5.00,1,0),('M002','2025-01-01',2800.00,56.00,2.00,1,1),('M002','2025-02-01',1500.00,30.00,2.00,1,0),('M003','2025-01-01',3200.00,240.00,7.50,1,1),('M003','2025-02-01',4500.00,337.50,7.50,1,0),('M005','2025-01-01',800.00,24.00,3.00,1,1),('M005','2025-02-01',1200.00,36.00,3.00,1,0);
/*!40000 ALTER TABLE `monthlydiscount` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order`
--

DROP TABLE IF EXISTS `order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order` (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order`
--

LOCK TABLES `order` WRITE;
/*!40000 ALTER TABLE `order` DISABLE KEYS */;
INSERT INTO `order` VALUES ('ORD001','2025-03-01','delivered','John Smith','2025-03-02','DHL','DHL123456','2025-03-05',450.00,22.50,427.50,'M001'),('ORD002','2025-03-05','dispatched','Sarah Johnson','2025-03-06','FedEx','FX789012','2025-03-10',780.00,39.00,741.00,'M001'),('ORD003','2025-03-10','dispatched','John Smith','2026-04-06','DHL','123456','2026-04-09',1250.00,0.00,1250.00,'M002'),('ORD004','2025-03-12','pending',NULL,NULL,NULL,NULL,NULL,560.00,0.00,560.00,'M002'),('ORD005','2025-03-15','delivered',NULL,NULL,NULL,NULL,NULL,3200.00,240.00,2960.00,'M003'),('ORD006','2025-03-18','processing',NULL,NULL,NULL,NULL,NULL,1850.00,55.50,1794.50,'M005'),('ORD007','2025-03-20','accepted',NULL,NULL,NULL,NULL,NULL,420.00,0.00,420.00,'M001');
/*!40000 ALTER TABLE `order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orderitem`
--

DROP TABLE IF EXISTS `orderitem`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orderitem` (
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
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orderitem`
--

LOCK TABLES `orderitem` WRITE;
/*!40000 ALTER TABLE `orderitem` DISABLE KEYS */;
INSERT INTO `orderitem` VALUES (1,20,10.00,200.00,'ORD001','100 00004'),(2,15,12.50,187.50,'ORD001','100 00001'),(3,10,6.25,62.50,'ORD001','200 00004'),(4,30,0.50,15.00,'ORD002','100 00002'),(5,25,1.20,30.00,'ORD002','100 00003'),(6,5,10.50,52.50,'ORD002','300 00001'),(7,10,2.50,25.00,'ORD002','200 00005'),(8,50,0.10,5.00,'ORD003','100 00001'),(9,30,0.50,15.00,'ORD003','100 00002'),(10,40,1.20,48.00,'ORD003','100 00003'),(11,20,0.30,6.00,'ORD003','200 00004'),(12,15,18.50,277.50,'ORD004','100 00005'),(13,10,25.00,250.00,'ORD004','100 00006'),(14,100,0.10,10.00,'ORD005','100 00001'),(15,80,0.50,40.00,'ORD005','100 00002'),(16,60,1.20,72.00,'ORD005','100 00003'),(17,30,10.00,300.00,'ORD005','100 00004'),(18,20,18.50,370.00,'ORD005','100 00005'),(19,15,25.00,375.00,'ORD005','100 00006'),(20,40,15.50,620.00,'ORD005','100 00007'),(21,25,19.50,487.50,'ORD005','100 00008'),(22,50,0.30,15.00,'ORD005','200 00004'),(23,30,2.50,75.00,'ORD005','200 00005'),(24,20,10.50,210.00,'ORD005','300 00001'),(25,15,15.00,225.00,'ORD005','300 00002'),(26,100,1.20,120.00,'ORD005','400 00001'),(27,80,1.30,104.00,'ORD005','400 00002'),(28,25,0.10,2.50,'ORD006','100 00001'),(29,20,0.50,10.00,'ORD006','100 00002'),(30,15,1.20,18.00,'ORD006','100 00003'),(31,10,10.00,100.00,'ORD006','100 00004'),(32,5,18.50,92.50,'ORD006','100 00005'),(33,30,0.10,3.00,'ORD007','100 00001'),(34,20,0.50,10.00,'ORD007','100 00002'),(35,10,1.20,12.00,'ORD007','100 00003');
/*!40000 ALTER TABLE `orderitem` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `overduepayment`
--

DROP TABLE IF EXISTS `overduepayment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `overduepayment` (
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
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `overduepayment`
--

LOCK TABLES `overduepayment` WRITE;
/*!40000 ALTER TABLE `overduepayment` DISABLE KEYS */;
INSERT INTO `overduepayment` VALUES (1,741.00,'2025-04-05','pending',0,NULL,NULL,NULL,'M001','INV002'),(2,1250.00,'2025-04-10','pending',0,'2025-04-12','2025-04-20',NULL,'M002','INV003'),(3,560.00,'2025-04-12','resolved',0,'2025-04-15','2025-04-25','2025-04-28','M002','INV004'),(4,1960.00,'2025-04-15','pending',0,NULL,NULL,NULL,'M003','INV005'),(5,1794.50,'2025-04-18','pending',0,NULL,NULL,NULL,'M005','INV006'),(6,420.00,'2025-04-20','pending',0,NULL,NULL,NULL,'M001','INV007');
/*!40000 ALTER TABLE `overduepayment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `recordpayment`
--

DROP TABLE IF EXISTS `recordpayment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `recordpayment` (
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
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `recordpayment`
--

LOCK TABLES `recordpayment` WRITE;
/*!40000 ALTER TABLE `recordpayment` DISABLE KEYS */;
INSERT INTO `recordpayment` VALUES (1,427.50,'2025-03-15','bank_transfer','BT-2025-001','INV001',4),(2,1000.00,'2025-03-20','card','CARD-1234-5678','INV005',4),(3,250.00,'2025-03-25','cheque','CHQ-001234','INV002',4),(4,500.00,'2025-03-28','bank_transfer','BT-2025-002','INV003',4),(5,741.00,'2026-04-06','bank_transfer','123456','INV002',1),(6,420.00,'2026-04-06','bank_transfer',NULL,'INV007',1),(7,420.00,'2026-04-06','bank_transfer',NULL,'INV-D45F8F2F',1);
/*!40000 ALTER TABLE `recordpayment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stockdelivery`
--

DROP TABLE IF EXISTS `stockdelivery`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `stockdelivery` (
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
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stockdelivery`
--

LOCK TABLES `stockdelivery` WRITE;
/*!40000 ALTER TABLE `stockdelivery` DISABLE KEYS */;
INSERT INTO `stockdelivery` VALUES (1,500,'2025-03-01','completed','PharmaSupply Ltd','PO-2025-001','100 00001',1),(2,300,'2025-03-05','completed','Medical Distributors Inc','PO-2025-002','100 00002',5),(3,200,'2025-03-10','completed','PharmaSupply Ltd','PO-2025-003','100 00003',5),(4,400,'2025-03-15','pending','Health Logistics','PO-2025-004','200 00004',5),(5,250,'2025-03-20','in_transit','Medical Distributors Inc','PO-2025-005','200 00005',5),(6,150,'2025-03-25','pending','PharmaSupply Ltd','PO-2025-006','300 00001',1),(7,200,'2026-03-27','completed','Manual Entry',NULL,'100 20002',1),(8,20,'2026-04-06','completed','Manual Entry',NULL,'3p14',1);
/*!40000 ALTER TABLE `stockdelivery` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `userlogin`
--

DROP TABLE IF EXISTS `userlogin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `userlogin` (
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
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `userlogin`
--

LOCK TABLES `userlogin` WRITE;
/*!40000 ALTER TABLE `userlogin` DISABLE KEYS */;
INSERT INTO `userlogin` VALUES (1,'admin','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9','John','Smith','admin@ipos.com','Administrator',1,NULL,NULL,NULL),(2,'director','9e4d7bba246abe731743986c4dc50897b68b1d0249a066abb3530fcbaa33dab3','Sarah','Johnson','director@ipos.com','Director of Operations',1,NULL,NULL,NULL),(3,'senior_acc','c395d384361300b04c5654cae54cef08da34cbdca32987723eabb9ac2804cbf2','David','Brown','senior.accountant@ipos.com','Senior Accountant',1,NULL,NULL,NULL),(4,'accountant','4d393ec34c3c6a875b95e66df5e6d6fc09efc33d66f12e3e98afca347d6b7638','Emma','Davis','accountant@ipos.com','Accountant',1,NULL,'123456789',''),(5,'warehouse','0e842cbe0341154ee33e0ed3bc18282cd69e016a8d56fda05ec92e7ff20a0f31','Mike','Wilson','warehouse@ipos.com','Warehouse Employee',1,NULL,NULL,NULL),(6,'delivery','d8541cd85756b64db7fa4d90d99ecd014e0ef76a1759afb343837e67a5bef29c','James','Taylor','delivery@ipos.com','Delivery Employee',1,NULL,NULL,NULL),(9,'SenAcc123','4a6619235b78c1bd9d14c17704c6410fbb9b2238461fedc43e41b6173e86fc3f','senior','accountant','email@email.com','Senior Accountant',1,NULL,NULL,NULL);
/*!40000 ALTER TABLE `userlogin` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-04-07 14:24:37
