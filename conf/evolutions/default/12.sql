# --- !Ups
ALTER TABLE "chirp"."user"
  ADD COLUMN "active" boolean NOT NULL;

# --- !Downs
ALTER TABLE "chirp"."user"
  DROP COLUMN "active";