-- MySQL dump 10.13  Distrib 8.0.25, for Win64 (x86_64)
--
-- Host: localhost    Database: doctorappointment
-- ------------------------------------------------------
-- Server version	8.0.25

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
-- Table structure for table `doctor`
--

DROP TABLE IF EXISTS `doctor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `doctor` (
  `doctorAMKA` varchar(11) NOT NULL,
  `username` varchar(45) NOT NULL,
  `hashedpassword` varchar(45) NOT NULL,
  `name` varchar(45) NOT NULL,
  `surname` varchar(45) NOT NULL,
  `specialty` varchar(45) NOT NULL,
  `ADMIN_username` varchar(45) DEFAULT NULL,
  `salt` varchar(45) DEFAULT NULL,
  `age` int NOT NULL,
  PRIMARY KEY (`doctorAMKA`,`username`),
  UNIQUE KEY `idDoctor_UNIQUE` (`doctorAMKA`),
  UNIQUE KEY `username_UNIQUE` (`username`),
  KEY `fk_DOCTOR_ADMIN1_idx` (`ADMIN_username`),
  CONSTRAINT `fk_DOCTOR_ADMIN1` FOREIGN KEY (`ADMIN_username`) REFERENCES `admin` (`username`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `doctor`
--

LOCK TABLES `doctor` WRITE;
/*!40000 ALTER TABLE `doctor` DISABLE KEYS */;
INSERT INTO `doctor` VALUES ('01119100203','gregdaniels','9934E473603E1BDDC2CE7EF03092A3F7','Greogory','Daniels','Pathologist','admin4','W,6<8f+@7K=6\'mo_',30),('04046903279','stpapad','8DCDAE8DA6AE7A1E23639E401FC27458','Stelios','Papadopoulos','Orthopedist','admin4','z\0E@xyg*0k�@G',52),('05026554433','m_rigos','B3C2C83BFE9C0ED0E7B0CE6A02C987F7','Menios','Rigos','Orthopedist','admin4','m\\,L5Jo[<H4Z-@)7Z',56),('19086208822','dr_k','70733061BEE9D12E6AF14DEEB126C3A5','Marios','Kalopoulos','Ophthalmologist','admin4','\"~7zRH\r�\0_\06*!	[z',59),('19099309918','doctor1','8229BAA4AB645E4C9A22C8911707AFF4','Kermie','Kegley','Pathologist','admin1','lrG{!]y_1U>z�//',28),('31128121619','manos_mp','DF9C3A9D5F15182F0F7D1C9A92C90F54','Emmanouil','Mpouzoukis','Orthopedist','admin4','gcDeuAst,b1%71w#H\"',40);
/*!40000 ALTER TABLE `doctor` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-07-14  3:16:39
