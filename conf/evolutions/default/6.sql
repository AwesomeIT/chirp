# --- !Ups
CREATE UNIQUE INDEX index_user_email ON "chirp"."user" (email);

# --- !Downs
DROP INDEX index_user_id_email;