package controllers.v1

import org.birdfeed.chirp.database.models.{ApiKey, User}
import org.birdfeed.chirp.test.BaseSpec
import play.api.libs.ws.WSClient

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class SampleControllerSpec extends BaseSpec {
  val wsClient = app.injector.instanceOf[WSClient]
  val testKey = ApiKey(true).create.key

  val uuid = java.util.UUID.randomUUID.toString
  val user = User(
    java.util.UUID.randomUUID.toString, s"${uuid}@uuid.com", uuid, 1
  ).create

  val payload = "your binary here".getBytes

  val created = Await.result(
    wsClient
      .url(s"http://localhost:${port}/v1/sample")
      .withHeaders("Chirp-Api-Key" -> testKey)
      .withQueryString(
        "user_id" -> user.id.toString,
        "file_name" -> "scala_test.wav"
      ).put(payload), Duration.Inf
  )
//
//  "PUT /v1/sample/create" should {
//    "create a new sample" in {
//      created.status must equal(201)
//    }
//  }

  "GET /v1/sample/:id" should {
    "retrieve a created sample" in {
      lazy val retrieved = Await.result(
        wsClient
          .url(s"http://localhost:${port}/v1/sample/${(created.json \ "id").get}")
          .withHeaders("Chirp-Api-Key" -> testKey)
          .get, Duration.Inf
      )

      retrieved.status must equal(200)
    }
  }

  "DELETE /v1/sample/:id" should {
    "delete a created sample" in {
      Await.result(
        wsClient.url(s"http://localhost:${port}/v1/sample/${(created.json \ "id").get}")
          .withHeaders("Chirp-Api-Key" -> testKey)
          .delete,
        Duration.Inf
      ).body.toInt must equal(1)
    }
  }

  "GET /v1/sample/:id/scores" should {
    "retrieve a list of scores" in {
      lazy val retrieved = Await.result (
        wsClient
          .url(s"http://localhost:${port}/v1/sample/${(created.json \ "id").get}/scores")
          .withHeaders("Chirp-Api-Key" -> testKey)
          .get, Duration.Inf
        )

      retrieved.status must equal(200)
    }
  }
}
