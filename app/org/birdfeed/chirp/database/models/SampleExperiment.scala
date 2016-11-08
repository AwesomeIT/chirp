package org.birdfeed.chirp.database.models

import com.google.inject.Inject
import org.birdfeed.chirp.database._
import org.birdfeed.chirp.database.models._
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.PostgresDriver.api._
import play.api.libs.json.{Json, Writes}

import scala.concurrent.Future
import scala.util.Try

/**
  * SampleExperiment relation instance representing one row object.
  *
  * @param slickTE Slick table element from codegenerated tables
  */
class SampleExperiment @Inject()(dbConfigProvider: DatabaseConfigProvider)(val slickTE: Tables.SampleExperiment#TableElementType) extends Tables.SampleExperimentRow(
  slickTE.id, slickTE.sampleId, slickTE.experimentId
) with Relation {

  // TODO: Fill this in!
  implicit val jsonWrites: Writes[this.type] = Writes { sample =>
    Json.obj(
      "id" -> id
    )
  }
}