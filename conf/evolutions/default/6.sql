# --- !Ups
ALTER TABLE User ADD   image_url varchar(100);
# --- !Downs
ALTER TABLE User DROP  image_url;
