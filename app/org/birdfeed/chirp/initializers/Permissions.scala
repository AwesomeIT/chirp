package org.birdfeed.chirp.initializers

import akka.actor.ActorSystem
import com.google.inject.{Inject, Singleton}
import org.birdfeed.chirp.database.models.{Permission, Role}
import play.api.{Application, Configuration, Environment, Mode}
import play.api.db.Database
import com.github.aselab.activerecord._
import com.github.aselab.activerecord.dsl._
import org.birdfeed.chirp.database.{SchemaTables} /* Do not remove, it scopes an implicit value */

/**
  * Map permissions from models.
  *
  * TODO: Use metaprogramming to retrieve the tables, or
  * hopefully Scala-ActiveRecord keeps a list of some sort
  *
  * http://stackoverflow.com/questions/19118283/how-can-i-get-all-object-vals-and-subobject-vals-using-reflection-in-scala
  *
  * Q: Why do we DI so many things?
  * A: The anatomy of a play application is not monolithic. We
  * have to wait for larger components to do some database operations.
  *
  * @param app Initialized Play Application
  *            (this is important as it ensures everything
  *            to do with the DB connection pool is
  *            bound at start, so it is dependency queued
  *            to run immediately after the Play application
  *            is ready)
  */
@Singleton
class Permissions @Inject()(
  val app: Application,
  val db: Database,
  actorSystem: ActorSystem,
  conf: Configuration,
  env: Environment,
  permission: Permission,
  role: Role
) {

  Permission.deleteAll
  Role.deleteAll

  // On self hatred and disgust
  db.withConnection { conn =>
    // Scala-ActiveRecord seems to not create a `serial`
    // so we don't get restart identity
    // TODO: Pull request!

    if (env.mode != Mode.Test) {
      conn.createStatement.execute(
        conn.nativeSQL("ALTER SEQUENCE permissions_id_seq RESTART WITH 1;") +
          conn.nativeSQL("ALTER SEQUENCE roles_id_seq RESTART WITH 1;") +
          conn.nativeSQL("TRUNCATE TABLE permissions_roles;")
      )
    }

  }


  val permissionable = Seq(
    "user", "accessToken", "apiKey", "sample", "experiment", "score"
  ).map { permission =>
    Seq(
      Permission(permission),
      Permission(s"$permission.write")
    )
  }

//  val administrator = Role("Administrator")
//  val researcher = Role("Researcher")
//  val participant = Role("Participant")

  var roles = Seq(
    Role("Administrator").create,
    Role("Researcher").create,
    Role("Participant").create
  )

  Role.inTransaction {
    roles = roles.map(SchemaTables.roles.insert)
  }

  roles(0).permissions ++= permissionable.flatten
  roles(1).permissions ++= permissionable.slice(3, 5).flatten
  roles(2).permissions ++= permissionable.last

  roles(0).save
  roles(1).save
  roles(2).save
}