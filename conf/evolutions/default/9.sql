# --- !Ups
create table begin (
  conversation_id int(10) not null auto_increment,
  image_url  varchar(100),
  twitter_id varchar(100),
  text       varchar(140),
  time       bigint(10),
  tweet_id   bigint(10),
  primary key(conversation_id)
);

# --- !Downs
drop table begin;
