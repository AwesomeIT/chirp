package controllers.v1

import org.birdfeed.chirp.database.models.{ApiKey, User}
import org.birdfeed.chirp.test.BaseSpec
import play.api.libs.json._
import play.api.libs.ws.WSClient

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class UserControllerSpec extends BaseSpec {
  val wsClient = app.injector.instanceOf[WSClient]

  val testKey = ApiKey(true).create.key

  "PUT /v1/user/create" should {
    "create a new user" in {
      lazy val username = java.util.UUID.randomUUID.toString
      lazy val email = s"${username}@email.com"

      lazy val response = Await.result(
        wsClient
          .url(s"http://localhost:${port}/v1/user")
          .withHeaders("Chirp-Api-Key" -> testKey)
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

    User("name", email, email).create

    "authenticate with the correct credentials" in {
      val response = Await.result(
        wsClient.url(s"http://localhost:${port}/v1/user/authenticate")
          .withHeaders("Chirp-Api-Key" -> testKey)
          .post(
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
        wsClient.url(s"http://localhost:${port}/v1/user/authenticate")
          .withHeaders("Chirp-Api-Key" -> testKey)
          .post(
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
      lazy val created = User("name", email, email, 1).create

      lazy val response = Await.result(
        wsClient.url(s"http://localhost:${port}/v1/user/${created.id}")
          .withHeaders("Chirp-Api-Key" -> testKey)
          .delete, Duration.Inf
      )

      response.status must equal(204)
    }
  }

  "GET /v1/user/:id/experiments" should {
    "get all experiments a user created" in {
      lazy val username = java.util.UUID.randomUUID.toString
      lazy val email = s"${username}@email.com"
      lazy val created = Await.result(
        User.create("name", email, email, 1), Duration.Inf
      ).get

      lazy val experiment = Await.result(
        Experiment.create(
          java.util.UUID.randomUUID.toString, created.id), Duration.Inf).get

      val response = Await.result(
        wsClient.url(s"http://localhost:${port}/v1/user/${created.id}/experiments")
          .withHeaders("Chirp-Api-Key" -> testKey)
          .get, Duration.Inf
      )

      response.status must equal(200)

    }
  }
}
