# --- !Ups
ALTER TABLE tweet ADD FOREIGN KEY(user_id) REFERENCES User(id);
# --- !Downs
ALTER TABLE tweet DROP FOREIGN KEY user_id;
