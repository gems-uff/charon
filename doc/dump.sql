
drop database charon;
create database charon;
grant all privileges on charon.* to charon@localhost identified by 'edPibTR1';

USE charon;

--
-- DROP ALL TABLES BEFORE CREATING
--

DROP TABLE IF EXISTS EXPERIMENT_INSTANCE;
DROP TABLE IF EXISTS EXPERIMENT_VERSION;
DROP TABLE IF EXISTS EXPERIMENT;
DROP TABLE IF EXISTS ARTIFACT_VALUE;
DROP TABLE IF EXISTS ARTIFACT_VALUE_LOCATION;
DROP TABLE IF EXISTS ARTIFACT_PORT_PROCESS_INSTANCE;
DROP TABLE IF EXISTS ARTIFACT_PORT_ACTIVITY_INSTANCE;
DROP TABLE IF EXISTS PROCESS_INSTANCE;
DROP TABLE IF EXISTS ACTIVITY_INSTANCE;
DROP TABLE IF EXISTS ACTIVITY_PORT;
DROP TABLE IF EXISTS PROCESS_PORT;
DROP TABLE IF EXISTS PROCESS;
DROP TABLE IF EXISTS ACTIVITY;
DROP TABLE IF EXISTS ARTIFACT;
DROP TABLE IF EXISTS PORT;
DROP TABLE IF EXISTS SYNCHRONISM;
DROP TABLE IF EXISTS DECISION;
DROP TABLE IF EXISTS OPTION_SELECTED;
DROP TABLE IF EXISTS OPTION_;
DROP TABLE IF EXISTS INITIAL;
DROP TABLE IF EXISTS FINAL;
DROP TABLE IF EXISTS SWFMS;
DROP TABLE IF EXISTS FLOW;
DROP TABLE IF EXISTS EXECUTION_STATUS;
DROP TABLE IF EXISTS ID_CONTROL;

--
-- TABLE DEFINITION FOR PROCESS
--

CREATE TABLE PROCESS
(
 id int unsigned not null,
 name varchar(255),
 type_ varchar(30),
 primary key (id)
) engine=InnoDB;

--
-- TABLE DEFINITION FOR ACTIVITY
--

CREATE TABLE ACTIVITY
(
 id int unsigned not null,
 name varchar(255),
 type_ varchar(30),
 primary key (id)
) engine=InnoDB;

--
-- TABLE DEFINITION FOR EXPERIMENT
--

CREATE TABLE EXPERIMENT
(
 id int unsigned not null,
 name varchar(255),
 primary key (id)
) engine=InnoDB;

--
-- TABLE DEFINITION FOR SWFMS
--

CREATE TABLE SWFMS
(
 id int unsigned not null,
 name varchar(255),
 host varchar(255),
 primary key (id)
) engine=InnoDB;

CREATE TABLE PROCESS_INSTANCE
(
 instance_id int unsigned not null,
 process_id int unsigned not null,
 name varchar(255),
 swfms_id int unsigned,
 primary key (instance_id),
 foreign key (process_id) references PROCESS (id),
 foreign key (swfms_id) references SWFMS (id)
) engine=InnoDB;


CREATE TABLE ACTIVITY_INSTANCE
(
 instance_id int unsigned not null,
 activity_id int unsigned not null,
 name varchar(255),
 primary key (instance_id),
 foreign key (activity_id) references ACTIVITY (id)
) engine=InnoDB;


CREATE TABLE EXPERIMENT_VERSION
(
 experiment int unsigned not null,
 version int unsigned not null,
 previous_version int unsigned,
 root_process int unsigned,
 primary key (experiment, version),
 foreign key (experiment, previous_version) references EXPERIMENT_VERSION (experiment, version),
 foreign key (experiment) references EXPERIMENT (id),
 foreign key (root_process) references PROCESS (id)
) engine=InnoDB;


CREATE TABLE EXPERIMENT_INSTANCE
(
 instance_id int unsigned not null,
 version_id int unsigned not null,
 experiment_id int unsigned not null,
 primary key (instance_id),
 foreign key (experiment_id, version_id) references EXPERIMENT_VERSION (experiment, version)
) engine=InnoDB;

--
-- TABLE DEFINITION FOR ARTIFACT
--

CREATE TABLE ARTIFACT
(
 id int unsigned not null,
 primary key (id)
) engine=InnoDB;

--
-- TABLE DEFINITION FOR ARTIFACT
--

CREATE TABLE PORT
(
 id int unsigned not null,
 type_ int unsigned,
 name varchar(255),
 data_type varchar(50),
 primary key (id)
) engine=InnoDB;

CREATE TABLE ACTIVITY_PORT
(
 activity int unsigned not null,
 port int unsigned not null,
 foreign key (activity) references ACTIVITY (id),
 foreign key (port) references PORT (id)
) engine=InnoDB;

CREATE TABLE PROCESS_PORT
(
 process int unsigned not null,
 port int unsigned not null,
 foreign key (process) references PROCESS (id),
 foreign key (port) references PORT (id)
) engine=InnoDB;

CREATE TABLE ARTIFACT_PORT_ACTIVITY_INSTANCE
(
 activity_instance int unsigned not null,
 artifact int unsigned not null,
 port int unsigned not null,
 foreign key (activity_instance) references ACTIVITY_INSTANCE (instance_id),
 foreign key (artifact) references ARTIFACT (id),
 foreign key (port) references PORT (id)
) engine=InnoDB;

CREATE TABLE ARTIFACT_PORT_PROCESS_INSTANCE
(
 process_instance int unsigned not null,
 artifact int unsigned not null,
 port int unsigned not null,
 foreign key (process_instance) references PROCESS_INSTANCE (instance_id),
 foreign key (artifact) references ARTIFACT (id),
 foreign key (port) references PORT (id)
) engine=InnoDB;

CREATE TABLE DECISION
(
 id int unsigned not null,
 name varchar(255),
 primary key (id)
) engine=InnoDB;

CREATE TABLE SYNCHRONISM
(
 id int unsigned not null,
 visible int unsigned not null,
 primary key (id)
) engine=InnoDB;

CREATE TABLE OPTION_
(
 id int unsigned not null,
 name varchar(255),
 primary key (id, name)
) engine=InnoDB;

CREATE TABLE OPTION_SELECTED
(
 id_option int unsigned not null,
 name varchar(255),
 path varchar(255),
 foreign key (id_option, name) references OPTION_ (id, name)
) engine=InnoDB;

CREATE TABLE INITIAL
(
 id int unsigned not null,
 primary key (id)
) engine=InnoDB;

CREATE TABLE FINAL
(
 id int unsigned not null,
 primary key (id)
) engine=InnoDB;

--
-- TABLE DEFINITION FOR CATEGORY AND CATEGORY MANY-TO-MANY ASSOCIATION
--
CREATE TABLE FLOW
(
 id_origin_element int unsigned not null,
 type_origin_element int unsigned not null,
 id_destination_element int unsigned not null,
 type_destination_element int unsigned not null
) engine=InnoDB;

--
-- TABLE DEFINITION FOR CATEGORY AND CATEGORY MANY-TO-MANY ASSOCIATION
--
CREATE TABLE EXECUTION_STATUS
(
 id_element int unsigned not null,
 type_element int unsigned not null,
 status smallint unsigned not null,
 start_time bigint unsigned,
 end_time bigint unsigned,
 path varchar(255),
 performers varchar(255)
) engine=InnoDB;

--
-- TABLE DEFINITION FOR ARTIFACT_VALUE
--

CREATE TABLE ARTIFACT_VALUE
(
 artifact int unsigned not null,
 value varchar(255),
 path varchar(255),
 foreign key (artifact) references ARTIFACT (id)
) engine=InnoDB;

--
-- TABLE DEFINITION FOR ARTIFACT_VALUE_LOCATION
--

CREATE TABLE ARTIFACT_VALUE_LOCATION
(
 artifact int unsigned not null,
 host_url varchar(255),
 host_local_path varchar(255),
 path varchar(255),
 foreign key (artifact) references ARTIFACT (id)
) engine=InnoDB;

--
-- TABLE DEFINITION FOR CATEGORY AND CATEGORY MANY-TO-MANY ASSOCIATION
--
CREATE TABLE ID_CONTROL
(
 last_id int unsigned not null
) engine=InnoDB;

insert into ID_CONTROL values(0);

delimiter |


CREATE TRIGGER update_last_id1 BEFORE INSERT ON EXPERIMENT
  FOR EACH ROW BEGIN
	IF NEW.id > (select last_id FROM ID_CONTROL) THEN  
    	UPDATE ID_CONTROL SET last_id = NEW.id;
   	END IF;
  END;
|

CREATE TRIGGER update_last_id3 BEFORE INSERT ON PROCESS
  FOR EACH ROW BEGIN
	IF NEW.id > (select last_id FROM ID_CONTROL) THEN  
    	UPDATE ID_CONTROL SET last_id = NEW.id;
   	END IF;
  END;
|

CREATE TRIGGER update_last_id4 BEFORE INSERT ON ACTIVITY
  FOR EACH ROW BEGIN
	IF NEW.id > (select last_id FROM ID_CONTROL) THEN  
    	UPDATE ID_CONTROL SET last_id = NEW.id;
   	END IF;
  END;
|

CREATE TRIGGER update_last_id5 BEFORE INSERT ON ARTIFACT
  FOR EACH ROW BEGIN
	IF NEW.id > (select last_id FROM ID_CONTROL) THEN  
    	UPDATE ID_CONTROL SET last_id = NEW.id;
   	END IF;
  END;
|

CREATE TRIGGER update_last_id6 BEFORE INSERT ON PORT
  FOR EACH ROW BEGIN
	IF NEW.id > (select last_id FROM ID_CONTROL) THEN  
    	UPDATE ID_CONTROL SET last_id = NEW.id;
   	END IF;
  END;
|

CREATE TRIGGER update_last_id7 BEFORE INSERT ON DECISION
  FOR EACH ROW BEGIN
	IF NEW.id > (select last_id FROM ID_CONTROL) THEN  
    	UPDATE ID_CONTROL SET last_id = NEW.id;
   	END IF;
  END;
|

CREATE TRIGGER update_last_id8 BEFORE INSERT ON SYNCHRONISM
  FOR EACH ROW BEGIN
	IF NEW.id > (select last_id FROM ID_CONTROL) THEN  
    	UPDATE ID_CONTROL SET last_id = NEW.id;
   	END IF;
  END;
|

CREATE TRIGGER update_last_id9 BEFORE INSERT ON OPTION_
  FOR EACH ROW BEGIN
	IF NEW.id > (select last_id FROM ID_CONTROL) THEN  
    	UPDATE ID_CONTROL SET last_id = NEW.id;
   	END IF;
  END;
|

CREATE TRIGGER update_last_id12 BEFORE INSERT ON SWFMS
  FOR EACH ROW BEGIN
	IF NEW.id > (select last_id FROM ID_CONTROL) THEN  
    	UPDATE ID_CONTROL SET last_id = NEW.id;
   	END IF;
  END;
|

CREATE TRIGGER update_last_id13 BEFORE INSERT ON PROCESS_INSTANCE
  FOR EACH ROW BEGIN
	IF NEW.instance_id > (select last_id FROM ID_CONTROL) THEN  
    	UPDATE ID_CONTROL SET last_id = NEW.instance_id;
   	END IF;
  END;
|

CREATE TRIGGER update_last_id14 BEFORE INSERT ON ACTIVITY_INSTANCE
  FOR EACH ROW BEGIN
	IF NEW.instance_id > (select last_id FROM ID_CONTROL) THEN  
    	UPDATE ID_CONTROL SET last_id = NEW.instance_id;
   	END IF;
  END;
  
|

 CREATE TRIGGER update_last_id15 BEFORE INSERT ON EXPERIMENT_INSTANCE
  FOR EACH ROW BEGIN
	IF NEW.instance_id > (select last_id FROM ID_CONTROL) THEN  
    	UPDATE ID_CONTROL SET last_id = NEW.instance_id;
   	END IF;
  END;

|  
delimiter ;


--INSERT INTO `PORT` VALUES (8,0,'portA','int');
--INSERT INTO `ACTIVITY` VALUES (5,'A1','local'),(6,'A2','local'),(7,'A3','local');
--INSERT INTO `ACTIVITY_INSTANCE` VALUES (10,5,'A1'),(11,6,'A2'),(12,7,'A3');
--INSERT INTO `ACTIVITY_PORT` VALUES (5,8);
--INSERT INTO `ARTIFACT` VALUES (9);
--INSERT INTO `ARTIFACT_PORT_ACTIVITY_INSTANCE` VALUES (10,9,8);
--INSERT INTO `EXECUTION_STATUS` VALUES (14,1,2,1270474393,1270474394,'',''),(13,2,2,1270474393,1270474394,'14',''),(10,3,2,1270474393,1270474394,'13, 14',''),(11,3,2,1270474394,1270474394,'13, 14',''),(12,3,2,1270474394,1270474394,'13, 14',''),(4,4,2,1270474394,1270474394,'13, 14','');
--INSERT INTO `FINAL` VALUES (4);
--INSERT INTO `FLOW` VALUES (4,4,4,7),(4,6,10,3),(11,3,4,4),(12,3,4,4),(10,3,11,3),(10,3,12,3);
--INSERT INTO `ID_CONTROL` VALUES (13);
--INSERT INTO `INITIAL` VALUES (4);
--INSERT INTO `SWFMS` VALUES (3,'Vistrails','192.168.0.1');
--INSERT INTO `PROCESS` VALUES (4,'P1','local');
--INSERT INTO `PROCESS_INSTANCE` VALUES (13,4,'P1',3);
--INSERT INTO `EXPERIMENT` VALUES (1);
--INSERT INTO `EXPERIMENT_VERSION` VALUES (2,NULL,1,'NACAD',13);
--INSERT INTO `EXPERIMENT_INSTANCE` VALUES (14,2);
--INSERT INTO `SYNCHRONISM` VALUES (4);