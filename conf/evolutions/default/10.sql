# --- !Ups
create table tweet (
  id int(10) not null auto_increment,
  user_id int(10) not null,
  image_url  varchar(100),
  twitter_id varchar(30),
  text varchar(140),
  time bigint(10),
  tweet_id bigint(10),
  reply bigint(10),
  conversation_id int(10),
  FOREIGN KEY(conversation_id) REFERENCES begin(conversationid),
  PRIMARY KEY(id)
);

# --- !Downs
drop table tweet;
