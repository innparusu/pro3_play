create table Mention (
  id int(10) not null auto_increment, 
  twitter_id  varchar(100)
  mention_id  bigint,
  createDate timestamp default current_timestamp(),
primary key(id));
# --- !Downs
drop table User;
