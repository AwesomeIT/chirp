# --- !Ups
CREATE INDEX index_user_id_email ON "chirp"."user" (id, email);

# --- !Downs
DROP INDEX index_user_id_email;