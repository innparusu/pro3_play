# --- !Ups
ALTER TABLE begin ADD FOREIGN KEY(user_id) REFERENCES User(id);
# --- !Downs
ALTER TABLE begin DROP FOREIGN KEY user_id;
