# --- !Ups

ALTER TABLE "chirp"."experiment"
  DROP COLUMN "start_date",
  DROP COLUMN "end_date",
  ADD COLUMN "user_id" integer references "chirp"."user"(id) NOT NULL;


ALTER TABLE "chirp"."sample"
  ADD COLUMN "active" boolean NOT NULL;

# --- !Downs
ALTER TABLE "chirp"."experiment"
  DROP COLUMN "user_id",
  ADD COLUMN "start_date" date NOT NULL,
  ADD COLUMN "end_date" date;

ALTER TABLE "chirp"."sample"
  DROP COLUMN "active";