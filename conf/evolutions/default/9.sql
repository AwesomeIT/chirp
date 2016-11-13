# --- !Ups
CREATE TABLE "chirp"."permission" (
  id serial not null,
  name varchar(255) not null,
  PRIMARY KEY (id)
);

CREATE TABLE "chirp"."role_permission" (
  id serial not null,
  role_id integer references "chirp"."role"("id") not null,
  permission_id integer references "chirp"."permission"("id") not null,
  PRIMARY KEY (id)
);

CREATE UNIQUE INDEX index_role_permission_role_id_permission_id ON "chirp"."role_permission" (role_id, permission_id);
CREATE UNIQUE INDEX index_permission_name ON "chirp"."permission" (name);

# --- !Downs
DROP TABLE "chirp"."permission" CASCADE;
DROP TABLE "chirp"."role_permission" CASCADE;