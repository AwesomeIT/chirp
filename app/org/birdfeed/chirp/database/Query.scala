package org.birdfeed.chirp.database

import com.google.inject.Inject
import play.api.Play
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._

import scala.concurrent.Future

sealed case class InjectedConfig @Inject()(dbConfigProvider: DatabaseConfigProvider)

object Query {
  val dbConfig = Play
    .current
    .injector
    .instanceOf[InjectedConfig]
    .dbConfigProvider
    .get[JdbcProfile]

  object User {
    final val tableQuery = TableQuery[Tables.User]

    def find(id: Int): Future[Option[Tables.User#TableElementType]] = {
      dbConfig.db.run(tableQuery.filter(_.id === id).result.headOption)
    }
  }
}
