package org.birdfeed.chirp.database.models

import com.google.inject.Inject
import org.birdfeed.chirp.database.{Query, Relation, Tables}
import slick.driver.PostgresDriver.api._
import org.birdfeed.chirp.database.Tables
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.{Json, Writes}
import slick.driver.JdbcProfile

import scala.concurrent.Future
import scala.util.Try

/**
  * User relation instance representing one row object.
  *
  * @param slickTE Slick table element from codegenerated tables
  */
class User @Inject()(val dbConfigProvider: DatabaseConfigProvider)(val slickTE: Tables.User#TableElementType) extends Tables.UserRow(
  slickTE.id, slickTE.name, slickTE.email, slickTE.bcryptHash, slickTE.roleId
) with Relation with Query {

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  implicit val jsonWrites: Writes[this.type] = Writes { user =>
    Json.obj(
      "id" -> id,
      "name" -> name,
      "email" -> email,
      "role_id" -> roleId
    )
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