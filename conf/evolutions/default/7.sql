# --- !Ups
CREATE TABLE "chirp"."oauth_access_token" (
  "id" serial NOT NULL,
  "token" VARCHAR(36) NOT NULL,
  "refresh_token" VARCHAR(36) NOT NULL,
  "life_seconds" BIGINT NOT NULL,
  --- TODO: Resource based scopes
  "role_id" INTEGER REFERENCES "chirp"."role"("id") NOT NULL,
  "expired" BOOLEAN NOT NULL,
  PRIMARY KEY ("id")
);

CREATE UNIQUE INDEX index_oauth_access_token_token ON "chirp"."oauth_access_token"("token");
CREATE UNIQUE INDEX index_oauth_access_token_refresh_token ON "chirp"."oauth_access_token"("refresh_token");


# --- !Downs
DROP INDEX index_oauth_access_token_token, index_oauth_access_token_refresh_token;
DROP TABLE "chirp"."oauth_access_token" CASCADE;