package org.birdfeed.chirp.database.models

import com.google.inject.Inject
import org.birdfeed.chirp.database._
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json._
import slick.driver.JdbcProfile
import org.birdfeed.chirp.database.Query
/**
  * Score relation instance representing one row object.
  *
  * @param slickTE Slick table element from codegenerated tables
  */
class Score @Inject()(val dbConfigProvider: DatabaseConfigProvider)(val slickTE: Tables.Score#TableElementType) extends Tables.ScoreRow(
  slickTE.id, slickTE.score, slickTE.sampleId, slickTE.experimentId, slickTE.userId
) with Relation[Tables#ScoreRow] with Query {

  /* Will fail without override, score is a decimal in the db and a BigDecimal in the object */
  override def equals(rhs: Any): Boolean = {
    if (rhs.getClass != this.getClass) { false }
    else {
      val cmp = rhs.asInstanceOf[this.type]
      slickTE.id == cmp.slickTE.id &&
        slickTE.score == cmp.slickTE.score &&
        slickTE.sampleId == cmp.slickTE.sampleId &&
        slickTE.experimentId == cmp.slickTE.experimentId &&
        slickTE.userId == cmp.slickTE.userId
    }
  }

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  // TODO: Fill this in!
  implicit val jsonWrites: Writes[this.type] = Writes { score =>
    Json.obj(
      "id" -> id,
      "score" -> this.score,
      "sample_id" -> sampleId,
      "experiment_id" -> experimentId,
      "user_id" -> userId
    )
  }
}