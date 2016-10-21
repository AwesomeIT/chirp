# --- !Ups
SET SEARCH_PATH TO "chirp";

ALTER TABLE "user"
  DROP COLUMN "role";

DROP TYPE Role;

CREATE TABLE "role" (
  "id" serial NOT NULL,
  "name" varchar(255) NOT NULL,
  PRIMARY KEY ("id")
);

ALTER TABLE "user"
  ADD COLUMN "role_id" INTEGER NOT NULL;

ALTER TABLE "user"
  ADD CONSTRAINT user_role_fk
  FOREIGN KEY("role_id")
  REFERENCES "chirp"."role" ("id")
  ON DELETE RESTRICT;

INSERT INTO "role" VALUES (DEFAULT, 'Administrator');
INSERT INTO "role" VALUES (DEFAULT, 'Researcher');
INSERT INTO "role" VALUES (DEFAULT, 'Evaluator');

# --- !Downs
SET SEARCH_PATH TO "chirp";
DROP TABLE "role";

CREATE TYPE Role AS ENUM ('Administrator', 'Researcher', 'Evaluator');

ALTER TABLE "user" DROP COLUMN "role_id";
ALTER TABLE "user" ADD COLUMN "role" Role NOT NULL;