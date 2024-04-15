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
CREATE TABLE  ` mediciones`(
`valueId` INT NOT NULL AUTO_INCREMEnT,
`medicionId`  INT NOT NULL,
`placaId`  INT NOT NULL,
` concentracion`  DOUBLE NOT NULL,
`fecha`  BIGINT  NOT NULL,
`groupId` INT NOT NULL,
PRIMARY KEY(`valueId`),
FOREIGN KEY(`placaId`)REFERENCES `placas`(`placaId`));

CREATE TABLE  `actuadores`(
`statusId`  INT NOT NULL AUTO_INCREMENT, 
`actuadorId`  INT NOT NULL ,
`placaId`  INT NOT NULL,
`statusValue`  BOOL NOT NULL,
fecha BIGINT  NOT NULL,
`groupId` INT NOT NULL,
PRIMARY KEY(`statusId`),
FOREIGN KEY(`placaId`)REFERENCES `placas`(`placaId`) );


