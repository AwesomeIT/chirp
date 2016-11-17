package org.birdfeed.chirp.database.models

import com.google.inject.Inject
import org.birdfeed.chirp.database.{Query, Relation, Tables}
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.{Json, Writes}
import slick.driver.JdbcProfile

/**
  * RolePermission relation instance representing one row object.
  *
  * @param slickTE Slick table element from codegenerated tables
  */
class RolePermission @Inject()(val dbConfigProvider: DatabaseConfigProvider)(val slickTE: Tables.RolePermission#TableElementType) extends Tables.RolePermissionRow(
  slickTE.id, slickTE.roleId, slickTE.permissionId) with Relation[Tables#RolePermissionRow] with Query {

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  implicit val jsonWrites: Writes[this.type] = Writes { user =>
    Json.obj(
      "id" -> id,
      "role_id" -> roleId,
      "permission_id" -> permissionId
    )
  }
}