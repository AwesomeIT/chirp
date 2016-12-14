package org.birdfeed.chirp.initializers

import java.sql.Timestamp

import com.google.inject.{Inject, Singleton}
import org.birdfeed.chirp.database.SchemaTables
import org.birdfeed.chirp.database.models.{AccessToken, Permission, Role, User}
import org.joda.time.DateTime
import play.api.db.Database
import play.api.{Environment, Mode}

/**
  * Map permissions from models.
  *
  * TODO: Use metaprogramming to retrieve the tables, or
  * hopefully Scala-ActiveRecord keeps a list of some sort
  *
  * http://stackoverflow.com/questions/19118283/how-can-i-get-all-object-vals-and-subobject-vals-using-reflection-in-scala
  */
@Singleton
class Permissions @Inject()(
  val db: Database,
  env: Environment
) {

  SchemaTables.initialize

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

  if (Permission.all.isEmpty) {
    val permissionable = Seq(
      "user", "accessToken", "apiKey", "sample", "experiment", "score"
    ).map { permission =>
      Seq(
        Permission(permission),
        Permission(s"$permission.write")
      )
    }

    val roles = Seq(
      Role("Administrator").create,
      Role("Researcher").create,
      Role("Participant").create
    )

    roles.head.permissions ++= permissionable.flatten
    roles(1).permissions += permissionable.head.head
    roles(2).permissions += permissionable.head.head
    roles(1).permissions ++= permissionable.slice(3, 5).flatten
    roles(2).permissions ++= permissionable.slice(3, 4).map(_.head)
    roles(2).permissions ++= permissionable(5)

    roles.head.save
    roles(1).save
    roles(2).save

    if (env.mode == Mode.Test) {
      lazy val user = User(
        java.util.UUID.randomUUID.toString, s"${java.util.UUID.randomUUID.toString}@test.com", "foo", roles.head.id
      ).create

      AccessToken.findByOrCreate(AccessToken(user.id,
        "testToken",
        "freshy",
        new Timestamp(DateTime.now.plusDays(1).getMillis)), "userId", "token", "refreshToken", "expiryDate")
    }
  }

  SchemaTables.cleanup
}