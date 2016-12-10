package controllers.v1

import org.birdfeed.chirp.database.models._
import org.birdfeed.chirp.test.BaseSpec

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class SampleControllerSpec extends BaseSpec {


  lazy val uuid = java.util.UUID.randomUUID.toString
  lazy val user = User(
    java.util.UUID.randomUUID.toString, s"${uuid}@uuid.com", uuid, 1
  ).create

  // TODO: Only fails in TEST
  //  lazy val created = Await.result(
  //    wsClient
  //      .url(s"http://localhost:${port}/v1/sample")
  //      .withHeaders("Content-Type" -> "application/octet-stream")
  //      .withQueryString(
  //        "user_id" -> user.id.toString,
  //        "file_name" -> "scala_test.wav"
  //      ).put(payload), Duration.Inf
  //  )
  //
  //  "PUT /v1/sample/create" should {
  //    "create a new sample" in {
  //      created.status must equal(201)
  //    }
  //  }

  lazy val created = Sample("foo", 1, "foo").create

  "GET /v1/sample/:id" should {
    "retrieve a created sample" in {
      lazy val retrieved = Await.result(
        wsClient
          .url(s"http://localhost:${port}/v1/sample/${created.id}")
          .withHeaders(
            "Chirp-Api-Key" -> testKey,
            "Chirp-Access-Token" -> "testToken"
          )
          .get, Duration.Inf
      )

      retrieved.status must equal(200)
    }
  }

  "DELETE /v1/sample/:id" should {
    "delete a created sample" in {
      Await.result(
        wsClient.url(s"http://localhost:${port}/v1/sample/${created.id}")
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
