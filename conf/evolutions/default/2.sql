# --- !Ups
ALTER TABLE User ADD   twitter_id varchar(100);
ALTER TABLE User ADD   access_token varchar(100);
ALTER TABLE User ADD   access_secret varchar(100);
ALTER TABLE User DROP  name;
ALTER TABLE User DROP  email;

# --- !Downs
ALTER TABLE User DROP  twitter_id;
ALTER TABLE User DROP  access_token;
ALTER TABLE User DROP  access_secret;
ALTER TABLE User ADD   name  varchar(100);
ALTER TABLE User ADD   email varchar(100);
