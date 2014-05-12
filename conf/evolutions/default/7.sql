# --- !Ups
ALTER TABLE Mention ADD user_id    int(100) not null;
ALTER TABLE Mention ADD image_url  varchar(100);
ALTER TABLE Mention ADD tweet_text varchar(140);
# --- !Downs
ALTER TABLE Mention DROP user_id    varchar(100);
ALTER TABLE Mention DROP image_url  varchar(100);
ALTER TABLE Mention DROP tweet_text varchar(140);
