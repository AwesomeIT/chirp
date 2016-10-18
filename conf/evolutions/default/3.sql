# --- !Ups
SET SEARCH_PATH TO "chirp";

CREATE TYPE Role AS ENUM ('Administrator', 'Researcher', 'Evaluator');

CREATE TABLE "chirp"."user" (
  "id" serial NOT NULL,
  "name" varchar(255) NOT NULL,
  "email" varchar(255) NOT NULL,
  "password" varchar(255) NOT NULL,
  "role" Role NOT NULL,
  PRIMARY KEY ("id")
);

ALTER TABLE "chirp"."sample"
  ADD CONSTRAINT sample_user_fk
  FOREIGN KEY ("user_id")
  REFERENCES "chirp"."user" ("id")
  ON DELETE CASCADE;

ALTER TABLE "chirp"."score"
  ADD CONSTRAINT score_user_fk
  FOREIGN KEY ("user_id")
  REFERENCES "chirp"."user" ("id")
  ON DELETE CASCADE;

# --- !Downs
DROP TABLE "chirp"."user" CASCADE;
DROP TYPE Role;