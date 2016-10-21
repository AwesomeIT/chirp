package org.birdfeed.chirp.database

import slick.driver.PostgresDriver.api._
import slick.driver.PostgresDriver

import org.scalatestplus.play._

import scala.concurrent.duration._
import scala.concurrent._


class QuerySpec extends PlaySpec with OneServerPerSuite {

  "Users" must {
    "be retrievable" in {
      val randomUuid: String = java.util.UUID.randomUUID.toString

      val retrievedUuid =
        Await.result[
          PostgresDriver.InsertActionExtensionMethods[
            (String, String, String, Int)
          ]#SingleInsertResult
        ](
          Query.dbConfig.db.run(
            Tables.User.map(
              (u: Tables.User) => (u.name, u.email, u.password, u.roleId)
            ) returning Tables.User.map(_.id) += (
              randomUuid, "a@a.com", "hash", 1
            )
          ), Duration.Inf
        ) match {
          case id: Int => {
            info(id.toString)
            Await.result[Option[Tables.User#TableElementType]](
              Query.User.find(id), Duration.Inf
            ) match {
              case Some(x) => x.name
              case None => fail("Record not found")
            }
          }
        }

      randomUuid must equal (retrievedUuid)
    }
  }
}
