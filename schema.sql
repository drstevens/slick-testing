create table `Group` (`ID` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,`NAME` VARCHAR(254) NOT NULL);
create table `Role` (`ID` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,`NAME` VARCHAR(254) NOT NULL);
create table `GroupRole` (`GROUP_ID` BIGINT NOT NULL,`ROLE_ID` BIGINT NOT NULL);
alter table `GroupRole` add constraint `PK_GROUPROLE` primary key(`GROUP_ID`,`ROLE_ID`);
alter table `GroupRole` add constraint `FK_GROUPROLE_GROUP` foreign key(`GROUP_ID`) references `Group`(`ID`) on update NO ACTION on delete NO ACTION;
alter table `GroupRole` add constraint `FK_GROUPROLE_ROLE` foreign key(`ROLE_ID`) references `Role`(`ID`) on update NO ACTION on delete NO ACTION;