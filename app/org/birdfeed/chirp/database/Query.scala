package org.birdfeed.chirp.database

import com.google.inject.Inject
import play.api.Play
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._

sealed case class InjectedConfig @Inject()(dbConfigProvider: DatabaseConfigProvider)

object Query {
  val dbConfig = Play
    .current
    .injector
    .instanceOf[InjectedConfig]
    .dbConfigProvider
    .get[JdbcProfile]

  object Sample {
    def find(id: Int): Future[Option[Sample]] = {
      where(_.id === id).asInstanceOf[Future[Option[Sample]]]
    }

    def where(predicate: Tables.Sample => Rep[Boolean]):
      Future[Option[Seq[Sample]]] = {
      dbConfig.db.run(Tables.Sample.filter(predicate).result).map(
        (rows: Seq[Tables.Sample#TableElementType]) => {
          Option(rows.map(new Sample(_)))
        }
      )
    }

    class Sample (var slickTableElement: Tables.Sample#TableElementType)
  }

  object User {
    def find(id: Int): Future[Option[User]] = {
      dbConfig.db.run(
        Tables.User.filter(_.id === id).result.headOption
      ).map(_.map(new User(_)))
    }

    def create(name: String, email: String, bcryptHash: String, roleId: Int): Future[Option[User]] = {
      dbConfig.db.run(
        Tables.User returning Tables.User.map(_.id) into (
          (user_row, id) => user_row.copy(id = id)
        ) += Tables.UserRow(0, name, email, bcryptHash, roleId)
      ).map((user_row: Tables.User#TableElementType) => {
        Option(new User(user_row))
      })
    }

    class User(var slickTableElement: Tables.User#TableElementType) {
      def reload: Future[Option[User]] = { find(slickTableElement.id) }

      def samples: Future[Option[Seq[Sample.Sample]]] = {
        Sample.where(_.userId === slickTableElement.id)
      }
    }
  }
}