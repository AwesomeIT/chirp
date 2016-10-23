package org.birdfeed.chirp.test.database

import org.scalatestplus.play._
import scala.concurrent.duration._
import scala.concurrent._

import org.birdfeed.chirp.database.Query

class QuerySpec extends PlaySpec with OneServerPerSuite {

  "Users" must {
    "be creatable and retrievable" in {
      val createdUser = Await.result(
        Query.User.create(
          java.util.UUID.randomUUID.toString, "email", "password", 1
        ), Duration.Inf
      ) match {
        case Some(user: Query.User.User) => user
        case None => fail("User not created")
      }

      val retrievedUser = Await.result(
        Query.User.find(createdUser.slickTableElement.id), Duration.Inf
      ) match {
        case Some(user: Query.User.User) => user
        case None => fail("Could not retrieve user")
      }

      createdUser must equal (retrievedUser)
    }

    "have working authentication" in {
      val password = java.util.UUID.randomUUID.toString

      val createdUser = Await.result(
        Query.User.create(
          "steve", "steve@mail.net", password, 1
        ), Duration.Inf
      ) match {
        case Some(user: Query.User.User) => user
        case None => fail("User not created")
      }

      val withAuthenticate = Await.result(
        Query.User.authenticate("steve@mail.net", password), Duration.Inf
      ) match {
        case Some(user: Query.User.User) => user
        case None => fail("User should have been found")
      }

      withAuthenticate must equal (createdUser)

      Await.result(
        Query.User.authenticate("steve@mail.net", "incorrect_password"),
        Duration.Inf
      ).isEmpty must be (true)
    }
  }
}
