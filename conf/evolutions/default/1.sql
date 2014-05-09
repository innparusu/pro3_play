# --- !Ups
create table User (
  id int(10) not null auto_increment, 
  name varchar(100),
  email varchar(100),
  createDate timestamp default current_timestamp(),
primary key(id));
# --- !Downs
drop table User;
