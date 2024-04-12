DROP DATABASE IF EXISTS Proyecto_DAD;
CREATE DATABASE IF NOT EXISTS Proyecto_DAD;
USE Proyecto_DAD;

DROP TABLE IF EXISTS  mediciones;
DROP TABLE  IF EXISTS actuadores; 
DROP TABLE IF EXISTS placas; 



CREATE TABLE  ` mediciones`(
`valueId` INT NOT NULL AUTO_INCREMEnT,
`medicionId`  INT NOT NULL,
`placaId`  INT NOT NULL,
` concentracion`  DOUBLE NOT NULL,
`fecha`  BIGINT  NOT NULL,
PRIMARY KEY(`valueId`));

CREATE TABLE  `actuadores`(
`statusId`  INT NOT NULL AUTO_INCREMENT, 
`actuadorId`  INT NOT NULL ,
`placaId`  INT NOT NULL,
`statusValue`  BOOL NOT NULL,
fecha BIGINT  NOT NULL,
PRIMARY KEY(`statusId`));


CREATE TABLE  `placas`(
`placaIdDB`  INT NOT NULL AUTO_INCREMENT,
`placaId`  INT NOT NULL,
PRIMARY KEY (`placaIdDB`));