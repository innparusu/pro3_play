# --- !Ups
ALTER TABLE begin ADD user_id int(10) not null 
# --- !Downs
ALTER TABLE begin DROP user_id int(10) not null 
