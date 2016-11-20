package org.birdfeed.chirp.database.models

import be.objectify.deadbolt.scala.models.Subject
import com.google.inject.Inject
import org.birdfeed.chirp.database.{Query, Relation, Tables}
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.{Json, Writes}
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.Try

/**
  * User relation instance representing one row object.
  *
  * @param slickTE Slick table element from codegenerated tables
  */
class User @Inject()(val dbConfigProvider: DatabaseConfigProvider)(val slickTE: Tables.User#TableElementType) extends Tables.UserRow(
  slickTE.id, slickTE.name, slickTE.email, slickTE.bcryptHash, slickTE.roleId, slickTE.active
) with Relation[Tables#UserRow] with Query with Subject {

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  val identifier = id.toString

  implicit val jsonWrites: Writes[this.type] = Writes { user =>
    Json.obj(
      "id" -> id,
      "name" -> name,
      "email" -> email,
      "role_id" -> roleId,
      "active" -> active
    )
  }

  // TODO: Maybe asynchronously handle these?
  /**
    * Deadbolt 2 roles listing.
    * @return
    */
  def roles: List[be.objectify.deadbolt.scala.models.Role] = {
    Await.result(
      Role.where(_.id == roleId), Duration.Inf
    ).get.toList
  }

  // TODO: Is this really the way we have to do a 'join'
  /**
    * Deadbolt 2 permissions listing.
    * @return
    */
  def permissions: List[be.objectify.deadbolt.scala.models.Permission] = {
    val permissionIds = Await.result(
      RolePermission.where(_.roleId == roleId).map(_.get.map(_.permissionId)),
      Duration.Inf
    )

    Await.result(
      Permission.where { permission => permissionIds.contains(permission.id) },
      Duration.Inf
    ).get.toList
  }

  /**
    * Query database for updated fields.
    * @return A new instance of this relation object.
    */
  def reload: Future[Try[User]] = { User.find(slickTE.id) }

  /**
    * Fetch samples associated with a user
    * @return Collection of Sample relations
    */
  def samples: Future[Try[Seq[Sample]]] = {
    Sample.where(_.userId === slickTE.id)
  }

  /**
    * Delete this instance of a user record
    * @return Potentially the ID of the deleted
    */
  def delete: Future[Try[Int]] = { User.delete(id) }
}