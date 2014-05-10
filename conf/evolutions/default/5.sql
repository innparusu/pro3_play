# --- !Ups
create table Mention (
  id int(10) not null auto_increment, 
  twitter_id  varchar(100),
  mention_id  bigint(10),
  createDate timestamp default current_timestamp(),
primary key(id));

create table User (
  id int(10) not null auto_increment, 
  twitter_id    varchar(100),
  access_token  varchar(100),
  access_secret varchar(100),
  createDate timestamp default current_timestamp(),
primary key(id));

# --- !Downs
drop table User;
