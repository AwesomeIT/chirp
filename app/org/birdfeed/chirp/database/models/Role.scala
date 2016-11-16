package org.birdfeed.chirp.database.models

import com.google.inject.Inject
import org.birdfeed.chirp.database.{Query, Relation, Tables}
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.{Json, Writes}
import slick.driver.JdbcProfile

/**
  * Role relation instance representing one row object.
  *
  * @param slickTE Slick table element from codegenerated tables
  */
class Role @Inject()(val dbConfigProvider: DatabaseConfigProvider)(val slickTE: Tables.Role#TableElementType) extends Tables.RoleRow(
  slickTE.id, slickTE.name) with Relation with Query with be.objectify.deadbolt.scala.models.Role {

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  implicit val jsonWrites: Writes[this.type] = Writes { user =>
    Json.obj(
      "id" -> id,
      "name" -> name
    )
  }


}