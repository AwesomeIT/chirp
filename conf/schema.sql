CREATE TABLE "public"."experiment" (
  "id" serial NOT NULL,
  "name" varchar(255) NOT NULL,
  "start_date" date NOT NULL,
  "end_date" date NOT NULL,
  "created_at" date NOT NULL,
  "updated_at" date NOT NULL,
  PRIMARY KEY ("id")
);

CREATE TABLE "public"."sample" (
  "id" serial NOT NULL,
  "name" varchar(255) NOT NULL,
  "user_id" integer NOT NULL, -- references user("id") DTO TBD
  "s3_url" varchar(255) NOT NULL,
  "created_at" date NOT NULL,
  "updated_at" date NOT NULL,
  PRIMARY KEY ("id")
);

CREATE TABLE "public"."sample_experiment" (
  "id" serial NOT NULL,
  "sample_id" integer references sample(id) NOT NULL,
  "experiment_id" integer references experiment(id) NOT NULL,
  PRIMARY KEY("id")
);

CREATE TABLE "public"."score" (
  "id" serial NOT NULL,
  -- Scores are scored 0.00 -> 1.00
  "score" decimal(2, 1) NOT NULL,
  "sample_experiment_id" integer references sample_experiment(id) NOT NULL,
  "user_id" integer NOT NULL, -- references user("id") DTO TBD
  PRIMARY KEY ("id")
);