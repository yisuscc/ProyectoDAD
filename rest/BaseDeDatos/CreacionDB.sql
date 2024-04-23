DROP DATABASE IF EXISTS Proyecto_DAD;
CREATE DATABASE IF NOT EXISTS Proyecto_DAD;
USE Proyecto_DAD;

DROP TABLE IF EXISTS  mediciones;
DROP TABLE  IF EXISTS actuadores; 
DROP TABLE IF EXISTS placas; 

CREATE TABLE  `placas`(
`placaIdDB`  INT NOT NULL AUTO_INCREMENT,
`placaId`  INT NOT NULL,
PRIMARY KEY (`placaIdDB`));
CREATE INDEX `placaId_idx` ON `placas` (`placaId`);#me lo ha dicho el gemini de google 
CREATE TABLE  `mediciones`(
`valueId` INT NOT NULL AUTO_INCREMEnT,
`medicionId`  INT NOT NULL,
`placaId`  INT NOT NULL,
`concentracion`  DOUBLE NOT NULL,
 `fecha` BIGINT  NOT NULL,
`groupId` INT NOT NULL,
PRIMARY KEY(`valueId`),
FOREIGN KEY(`placaId`)REFERENCES `placas`(`placaId`));

CREATE TABLE  `actuadores`(
`statusId`  INT NOT NULL AUTO_INCREMENT, 
`actuadorId`  INT NOT NULL ,
`placaId`  INT NOT NULL,
`statusValue`  BOOL NOT NULL,
`fecha` BIGINT  NOT NULL,
`groupId` INT NOT NULL,
PRIMARY KEY(`statusId`),
FOREIGN KEY(`placaId`)REFERENCES `placas`(`placaId`) );

INSERT INTO placas(placaId) VALUES (0),(1),(2);
INSERT INTO Proyecto_DAD.actuadores(actuadorId, placaId, statusValue, fecha, groupId) VALUES
(0,0,true,UNIX_TIMESTAMP(NOW()),0),
(0,0,true,UNIX_TIMESTAMP(NOW())+1,0),
(1,1,true,UNIX_TIMESTAMP(NOW()),1),
(1,1,true,UNIX_TIMESTAMP(NOW())+1,1),
(2,2,true,UNIX_TIMESTAMP(NOW()),2),
(2,2,true,UNIX_TIMESTAMP(NOW())+1,2);
INSERT INTO Proyecto_DAD.mediciones(medicionId, placaId, concentracion, fecha, groupId) VALUES 
(0,0,FLOOR(RAND() * 1000),UNIX_TIMESTAMP(NOW()),0),
(0,0,FLOOR(RAND() * 1000),UNIX_TIMESTAMP(NOW())+1,0),
(1,1,FLOOR(RAND() * 1000),UNIX_TIMESTAMP(NOW()),1),
(1,1,FLOOR(RAND() * 1000),UNIX_TIMESTAMP(NOW())+1,1),
(2,2,FLOOR(RAND() * 1000),UNIX_TIMESTAMP(NOW()),2),
(2,2,FLOOR(RAND() * 1000),UNIX_TIMESTAMP(NOW())+1,2);

