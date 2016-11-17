package org.birdfeed.chirp.database.models

import com.google.inject.Inject
import org.birdfeed.chirp.database.{Query, Relation, Tables}
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.{Json, Writes}
import slick.driver.JdbcProfile

/**
  * Permission relation instance representing one row object.
  *
  * @param slickTE Slick table element from codegenerated tables
  */
class Permission @Inject()(val dbConfigProvider: DatabaseConfigProvider)(val slickTE: Tables.Permission#TableElementType) extends Tables.PermissionRow(
  slickTE.id, slickTE.name) with Relation[Tables#Permission] with Query with be.objectify.deadbolt.scala.models.Permission {

  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val value = name

  implicit val jsonWrites: Writes[this.type] = Writes { user =>
    Json.obj(
      "id" -> id,
      "name" -> name
    )
  }


}