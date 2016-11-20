package org.birdfeed.chirp.database.models

import com.google.inject.Inject
import org.birdfeed.chirp.database._
import org.birdfeed.chirp.database.models._
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.PostgresDriver.api._
import play.api.libs.json.{Json, Writes}
import slick.driver.JdbcProfile

import scala.concurrent.Future
import scala.util.Try

/**
  * Sample relation instance representing one row object.
  *
  * @param slickTE Slick table element from codegenerated tables
  */
class Experiment @Inject()(val dbConfigProvider: DatabaseConfigProvider)(val slickTE: Tables.Experiment#TableElementType) extends Tables.ExperimentRow(
  slickTE.id, slickTE.name, slickTE.createdAt, slickTE.updatedAt, slickTE.userId
) with Relation[Tables#ExperimentRow] with org.birdfeed.chirp.database.Query {

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  override def equals(rhs: Any): Boolean = {
    if (rhs.getClass != this.getClass) { false }
    else {
      val cmp = rhs.asInstanceOf[this.type]
      slickTE.id == cmp.slickTE.id &&
      slickTE.name == cmp.slickTE.name &&
      slickTE.userId == cmp.slickTE.userId
    }
  }

  def <<(sample: Sample): Future[Try[SampleExperiment]] = {
    SampleExperiment.create(sample.id, id)
  }

  // TODO: Fill this in!
  implicit val jsonWrites: Writes[this.type] = Writes { sample =>
    Json.obj(
      "id" -> id,
      "name" -> name,
      "createdAt" -> createdAt,
      "updatedAt" -> updatedAt,
      "user_id" -> userId
    )
  }
}