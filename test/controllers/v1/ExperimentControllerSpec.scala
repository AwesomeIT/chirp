package controllers.v1

import org.birdfeed.chirp.database.models.{AccessToken, User}
import org.birdfeed.chirp.test.BaseSpec
import play.api.libs.json._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class ExperimentControllerSpec extends BaseSpec {

  lazy val uuid = java.util.UUID.randomUUID.toString
  lazy val user = AccessToken.findBy("token", "testToken")
    .map(_.user.toOption)
    .head.get

  lazy val created = Await.result(
    wsClient
      .url(s"http://localhost:${port}/v1/experiment")
      .withHeaders(
        "Chirp-Api-Key" -> testKey,
        "Chirp-Access-Token" -> "testToken"
      )
      .put(Json.obj(
        "name" -> "name",
        "userId" -> user.id
      )), Duration.Inf)


  "PUT /v1/experiment" should {
    "create a new experiment" in {
      created.status must equal(201)
    }
  }

  "GET /v1/experiment/:id" should {
    "retrieve a created experiment" in {
      Await.result(
        wsClient
          .url(s"http://localhost:${port}/v1/experiment/${(created.json \ "id").get}")
          .withHeaders(
            "Chirp-Api-Key" -> testKey,
            "Chirp-Access-Token" -> "testToken"
          )
          .get, Duration.Inf
      ).status must equal(200)
    }
  }

  "DELETE /v1/experiment/:id" should {
    "delete a created experiment" in {
      Await.result(
        wsClient.url(s"http://localhost:${port}/v1/experiment/${(created.json \ "id").get}")
          .withHeaders(
            "Chirp-Api-Key" -> testKey,
            "Chirp-Access-Token" -> "testToken"
          )
          .delete,
        Duration.Inf
      ).status must equal(204)
    }
  }
}
