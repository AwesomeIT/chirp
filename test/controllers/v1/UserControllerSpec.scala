package controllers.v1

import org.scalatestplus.play._
import play.api.libs.json._
import play.api.libs.ws.WSClient
import play.api.db.slick.DatabaseConfigProvider

import slick.driver.JdbcProfile

import scala.concurrent.Await
import scala.concurrent.duration.Duration

import org.birdfeed.chirp.database.Query

class UserControllerSpec extends PlaySpec with OneServerPerSuite with Query {
  val wsClient = app.injector.instanceOf[WSClient]

  val dbConfigProvider = app.injector.instanceOf(classOf[DatabaseConfigProvider])
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  "PUT /v1/user/create" should {
    "create a new user" in {
      lazy val username = java.util.UUID.randomUUID.toString
      lazy val email = s"${username}@email.com"

      lazy val response = Await.result(
        wsClient
          .url(s"http://localhost:${port}/v1/user")
          .put(Json.obj(
                          "name" -> username,
                          "email" -> email,
                          "password" -> "hunter12"
      )), Duration.Inf)

      response.status must equal(201)
    }
  }

  // We are using the email as the passwords in these
  // tests because it really doesn't matter

  "POST /v1/user/authenticate" should {

    lazy val username = java.util.UUID.randomUUID.toString
    lazy val email = s"${username}@email.com"

    Await.result(
      User.create("name", email, email, 1), Duration.Inf
    )

    "authenticate with the correct credentials" in {
      val response = Await.result(
        wsClient.url(s"http://localhost:${port}/v1/user/authenticate").post(
          Json.obj(
            "email" -> email,
            "password" -> email
          )
        ), Duration.Inf
      )

      response.status must equal(200)
    }

    "not authenticate with incorrect credentials" in {
      lazy val response = Await.result(
        wsClient.url(s"http://localhost:${port}/v1/user/authenticate").post(
          Json.obj(
            "email" -> email,
            "password" -> "obviously_incorrect_password"
          )
        ), Duration.Inf
      )

      response.status must equal(401)
    }
  }

  "DELETE /v1/user/:id" should {
    "delete an existing user" in {
      lazy val username = java.util.UUID.randomUUID.toString
      lazy val email = s"${username}@email.com"
      lazy val created = Await.result(
        User.create("name", email, email, 1), Duration.Inf
      ).get

      lazy val response = Await.result(
        wsClient.url(s"http://localhost:${port}/v1/user/${created.id}").delete,
        Duration.Inf
      )

      response.status must equal(200)
      response.body must equal("1") // record(s) deleted
    }
  }
}
