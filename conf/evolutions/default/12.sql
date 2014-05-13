# --- !Ups
ALTER TABLE Mention ADD FOREIGN KEY(user_id) REFERENCES User(id);
# --- !Downs
ALTER TABLE Mention DROP FOREIGN KEY user_id;
