package org.birdfeed.chirp.database.models

import com.google.inject.Inject
import org.birdfeed.chirp.database.{Query, Relation, Tables}
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.{Json, Writes}
import slick.driver.JdbcProfile

/**
  * ApiKey relation instance representing one row object.
  *
  * @param slickTE Slick table element from codegenerated tables
  */
class ApiKey @Inject()(val dbConfigProvider: DatabaseConfigProvider)(val slickTE: Tables.ApiKey#TableElementType) extends Tables.ApiKeyRow(
  slickTE.key, slickTE.active) with Relation[Tables#ApiKeyRow] with Query {

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  implicit val jsonWrites: Writes[this.type] = Writes { user =>
    Json.obj(
      "key" -> key,
      "active" -> active.toString
    )
  }
}