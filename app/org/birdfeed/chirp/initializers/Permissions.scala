package org.birdfeed.chirp.initializers

import com.google.inject.{Inject, Singleton}
import org.birdfeed.chirp.database.models.{Permission, Role}
import play.api.Application
import play.api.db.Database

/**
  * Map permissions from models.
  *
  * TODO: Use metaprogramming to retrieve the tables, or
  * hopefully Scala-ActiveRecord keeps a list of some sort
  *
  * http://stackoverflow.com/questions/19118283/how-can-i-get-all-object-vals-and-subobject-vals-using-reflection-in-scala
  * @param app Initialized Play Application
  *            (this is important as it ensures everything
  *            to do with the DB connection pool is
  *            bound at start, so it is dependency queued
  *            to run immediately after the Play application
  *            is ready)
  */
@Singleton
class Permissions @Inject()(val app: Application, val db: Database) {
  Permission.deleteAll
  Role.deleteAll


  // On self hatred and disgust
  db.withConnection { conn =>
    // Scala-ActiveRecord seems to not create a `serial`
    // so we don't get restart identity
    // TODO: Pull request!
    conn.createStatement.execute(
      conn.nativeSQL("ALTER SEQUENCE permissions_id_seq RESTART WITH 1;") +
        conn.nativeSQL("ALTER SEQUENCE roles_id_seq RESTART WITH 1;") +
        conn.nativeSQL("TRUNCATE TABLE permissions_roles;")
    )
  }

  val permissionable = Seq(
    "user", "accessToken", "apiKey", "sample", "experiment", "score"
  ).map { permission =>
    Seq(
      Permission(permission).create,
      Permission(s"$permission.write").create
    )
  }

  val administrator = Role("Administrator")
  val researcher = Role("Researcher")
  val participant = Role("Participant")

  administrator.create.permissions ++= permissionable.flatten
  researcher.create.permissions ++= permissionable.slice(3, 5).flatten
  participant.create.permissions ++= permissionable.last

  administrator.save
  researcher.save
  participant.save
}