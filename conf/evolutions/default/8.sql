# --- !Ups
SET SEARCH_PATH TO "chirp";

ALTER TABLE "score"
  DROP COLUMN "sample_experiment_id",
  ADD COLUMN "sample_id" integer references sample(id) NOT NULL,
  ADD COLUMN "experiment_id" integer references experiment(id) NOT NULL;

# --- !Downs
ALTER TABLE "score"
  ADD COLUMN "sample_experiment_id" integer references sample_experiment(id) NOT NULL,
  DROP COLUMN "sampleId",
  DROP COLUMN "experimentId";