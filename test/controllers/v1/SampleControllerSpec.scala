package controllers.v1

import org.birdfeed.chirp.database.Query
import org.birdfeed.chirp.support.test.BaseSpec
import org.scalatest.{AsyncWordSpec, MustMatchers, ParallelTestExecution}
import org.scalatestplus.play._
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.ws.WSClient
import slick.driver.JdbcProfile

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class SampleControllerSpec extends BaseSpec {
  val wsClient = app.injector.instanceOf[WSClient]

  val uuid = java.util.UUID.randomUUID.toString
  val user = Await.result(User.create(
    java.util.UUID.randomUUID.toString, s"${uuid}@uuid.com", uuid, 1
  ), Duration.Inf).get

  val payload = "your binary here".getBytes
  lazy val created = Await.result(
    wsClient
      .url(s"http://localhost:${port}/v1/sample")
      .withQueryString(
        "user_id" -> user.id.toString,
        "file_name" -> "scala_test.wav"
      ).put(payload), Duration.Inf)

  "PUT /v1/sample/create" should {
    "create a new sample" in {
      wsClient
        .url(s"http://localhost:${port}/v1/sample")
        .withQueryString(
          "user_id" -> user.id.toString,
          "file_name" -> "scala_test.wav"
        ).put(payload).map { response => response.status must equal(201) }
    }
  }

  "GET /v1/sample/:id" should {
    "retrieve a created sample" in {
      wsClient
        .url(s"http://localhost:${port}/v1/sample/${(created.json \ "id").get}")
        .get.map { response => response.status must equal(200) }
    }
  }

  "DELETE /v1/sample/:id" should {
    "delete a created sample" in {
      wsClient
        .url(s"http://localhost:${port}/v1/sample/${(created.json \ "id").get}")
        .delete.map { response => response.body.toInt must equal(1) /* records deleted */ }
    }
  }
}
