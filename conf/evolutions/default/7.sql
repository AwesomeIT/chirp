# --- !Ups
ALTER TABLE "chirp"."experiment" ALTER COLUMN end_date DROP NOT NULL;

# --- !Downs
ALTER TABLE "chirp"."experiment" ALTER COLUMN end_date SET NOT NULL;
