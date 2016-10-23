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
          java.util.UUID.randomUUID.toString, "some hash", "email", 1
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

      (createdUser.slickTableElement) must equal (retrievedUser.slickTableElement)

    }
  }
}
