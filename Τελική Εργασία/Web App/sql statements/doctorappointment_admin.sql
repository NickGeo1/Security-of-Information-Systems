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
-- Table structure for table `admin`
--

DROP TABLE IF EXISTS `admin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admin` (
  `username` varchar(45) NOT NULL,
  `hashedpassword` varchar(45) NOT NULL,
  `salt` varchar(45) DEFAULT NULL,
  `age` int NOT NULL,
  `name` varchar(45) NOT NULL,
  `surname` varchar(45) NOT NULL,
  PRIMARY KEY (`username`),
  UNIQUE KEY `username_UNIQUE` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin`
--

LOCK TABLES `admin` WRITE;
/*!40000 ALTER TABLE `admin` DISABLE KEYS */;
INSERT INTO `admin` VALUES ('admin1','5049EF64C2A9C559491A563D243C8BA0','\'Puz}.6a~4M\ZhdCg',25,'Michael','Rogers'),('admin2','596FF52FDFD9F36884EBD22741FA7086','% Ry@KA@.\'	a~3Q@',50,'Isak','Stagge'),('admin3','ABBEF9BE3AFE7D4BCDD60AA6EA3F52FA',']\0gQD\n!7FgNwvo5l:',36,'Gifford','Dyet'),('admin4','E7BCF4F2D17DE0B86807735C541F4753','T}I1#e*6v:}l}=>{k',19,'Hildy','Carrel'),('chrtai','F40F84759D2BEEE46CD931B948E621A4','\nu!dyGY|jrkW;p',24,'Chrissie','Taillard'),('drpsos','457C3ECDAE7455C663EC6673DBE39FD0',' K$),h4#!a{\"<)l�',19,'Dorise','Ionn'),('louisa99','C6434601481D75800390EBF47901290B','	DwdWI	^MROm\rq\'',28,'Loise','Dabnot'),('maurene21','503EF49687817A90ECAE22C257F3F547','	Xas):/	o#-+#	',33,'Maurene','Labarre'),('tn02_.','3FB08D3259C056CD03053395AB9183D7','qr.]:Gjiy\r.5e3@6',19,'Toni','Gaughan'),('__hamel__','5B35D6E92337A91413F336BB7096DE46','[Baj,,)v8	R\'qnf5~',44,'Hamel','Godier');
/*!40000 ALTER TABLE `admin` ENABLE KEYS */;
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
