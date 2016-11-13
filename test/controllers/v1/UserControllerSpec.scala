package controllers.v1

import org.birdfeed.chirp.database.Query
import org.birdfeed.chirp.support.test.BaseSpec
import org.scalatest.{AsyncWordSpec, MustMatchers, ParallelTestExecution}
import org.scalatestplus.play._
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json._
import play.api.libs.ws.WSClient
import slick.driver.JdbcProfile

class UserControllerSpec extends BaseSpec {
  val wsClient = app.injector.instanceOf[WSClient]

  "PUT /v1/user/create" should {
    "create a new user" in {
      lazy val username = java.util.UUID.randomUUID.toString
      lazy val email = s"${username}@email.com"

      wsClient
        .url(s"http://localhost:${port}/v1/user")
        .put(Json.obj(
          "name" -> username,
          "email" -> email,
          "password" -> "hunter12"
        )).map { response => response.status must equal(201) }
    }
  }

  // We are using the email as the passwords in these
  // tests because it really doesn't matter

  "POST /v1/user/authenticate" should {
    lazy val username = java.util.UUID.randomUUID.toString
    lazy val email = s"${username}@email.com"

    User.create("name", email, email, 1)

    "authenticate with the correct credentials" in {
      wsClient.url(s"http://localhost:${port}/v1/user/authenticate").post(
        Json.obj(
          "email" -> email,
          "password" -> email
        )
      ).map { response => response.status must equal(200) }
    }

    "not authenticate with incorrect credentials" in {
      wsClient.url(s"http://localhost:${port}/v1/user/authenticate").post(
        Json.obj(
          "email" -> email,
          "password" -> "obviously_incorrect_password"
        )
      ).map { response => response.status must equal(401) }
    }
  }

  "DELETE /v1/user/:id" should {
    "delete an existing user" in {
      lazy val username = java.util.UUID.randomUUID.toString
      lazy val email = s"${username}@email.com"
      val created = User.create("name", email, email, 1)

      wsClient
        .url(s"http://localhost:${port}/v1/user/${created.map(_.map(_.id))}")
        .delete.map { response => response.body.toInt must equal(1) /* record(s) deleted */ }
    }
  }
}
