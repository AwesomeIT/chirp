# --- !Ups
CREATE INDEX index_score_sample_experiment ON "chirp"."score" (id, sample_experiment_id);

# --- !Downs
DROP INDEX index_score_sample_experiment;