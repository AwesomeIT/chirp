package controllers.v1

import org.birdfeed.chirp.database.models.User
import org.birdfeed.chirp.test.BaseSpec
import play.api.libs.json._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class UserControllerSpec extends BaseSpec {

  "PUT /v1/user/create" should {
    "create a new user" in {
      lazy val username = java.util.UUID.randomUUID.toString
      lazy val email = s"${username}@email.com"

      lazy val response = Await.result(
        wsClient
          .url(s"http://localhost:${port}/v1/user")
          .withHeaders(
            "Chirp-Api-Key" -> testKey,
            "Chirp-Access-Token" -> "testToken"
          )
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
          .withHeaders(
            "Chirp-Api-Key" -> testKey,
            "Chirp-Access-Token" -> "testToken"
          )
          .delete, Duration.Inf
      )

      response.status must equal(204)
    }
  }
}
