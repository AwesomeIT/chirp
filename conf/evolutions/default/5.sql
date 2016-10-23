# ---!Ups
SET SEARCH_PATH TO "chirp";

ALTER TABLE "user"
    RENAME COLUMN password TO bcrypt_hash;

# ---!Downs

SET SEARCH_PATH TO "chirp";

ALTER TABLE "user"
  RENAME COLUMN bcrypt_hash TO password;