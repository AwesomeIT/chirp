# ---!Ups

CREATE TABLE "chirp"."access_token" (
  user_id integer not null references "chirp"."user"(id),
  token varchar(36) not null,
  refresh_token varchar(36) not null,
  issue_time date not null,
  expires_in date not null,
  primary key (token)
);

CREATE UNIQUE INDEX index_access_token_refresh_token ON "chirp"."access_token"(refresh_token);

# ---!Downs
DROP TABLE "chirp"."access_token" CASCADE;

