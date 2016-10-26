package org.birdfeed.chirp.database

import com.google.inject.Inject
import play.api.Play
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._

import com.github.t3hnar.bcrypt._

sealed case class InjectedConfig @Inject()(dbConfigProvider: DatabaseConfigProvider)

object Query {
  val dbConfig = Play
    .current
    .injector
    .instanceOf[InjectedConfig]
    .dbConfigProvider
    .get[JdbcProfile]

  trait Support[T] {
    var slickTE: T

    override def equals(right: Any): Boolean = {
      if (right.getClass != this.getClass) {
        false
      } else { slickTE == right.asInstanceOf[this.type] }
    }
  }

  object Sample extends {
    def find(id: Int): Future[Option[Sample]] = { where(_.id === id).map(_.map(_.headOption)).map(_.head) }

    def where(predicate: Tables.Sample => Rep[Boolean]): Future[Option[Seq[Sample]]] = {
      dbConfig.db.run(Tables.Sample.filter(predicate).result).map(
        (rows: Seq[Tables.Sample#TableElementType]) => {
          Option(rows.map(new Sample(_)))
        }
      )
    }

    class Sample (var slickTE: Tables.Sample#TableElementType) extends Tables.SampleRow(
      slickTE.id, slickTE.name, slickTE.userId, slickTE.s3Url, slickTE.createdAt, slickTE.updatedAt
    ) with Support[Tables.Sample#TableElementType]
  }

  object User {
    def find(id: Int): Future[Option[User]] = { where(_.id === id).map(_.map(_.headOption)).map(_.head) }

    def where(predicate: Tables.User => Rep[Boolean]): Future[Option[Seq[User]]] = {
      dbConfig.db.run(Tables.User.filter(predicate).result).map(
        (rows: Seq[Tables.User#TableElementType]) => {
          Option(rows.map(new User(_)))
        }
      )
    }

    def authenticate(email: String, password: String): Future[Option[User]] = {
      where(_.email === email).map(_.map(_.filter(
        (user: User) => password.isBcrypted(user.bcryptHash)
      ))).map(_.map(_.headOption)).map(_.head)
    }

    def create(name: String, email: String, password: String, roleId: Int): Future[Option[User]] = {
      dbConfig.db.run(
        Tables.User returning Tables.User.map(_.id) into (
          (user_row, id) => user_row.copy(id = id)
          ) += Tables.UserRow(0, name, email, password.bcrypt, roleId)
      ).map((user_row: Tables.User#TableElementType) => {
        Option(new User(user_row))
      })
    }

    class User(var slickTE: Tables.User#TableElementType) extends Tables.UserRow(
      slickTE.id, slickTE.name, slickTE.email, slickTE.bcryptHash, slickTE.roleId
    ) with Support[Tables.User#TableElementType] {
      def reload: Future[Option[User]] = { find(slickTE.id) }

      def samples: Future[Option[Seq[Sample.Sample]]] = {
        Sample.where(_.userId === slickTE.id)
      }
    }
  }
}