package controllers.v1

import awscala.s3.{Bucket, PutObjectResult}
import org.scalatestplus.play._
import play.api.libs.json._
import play.api.libs.ws.WSClient
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import org.birdfeed.chirp.database.Query
import org.birdfeed.chirp.adapter._
import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfter, MustMatchers, WordSpec}

class SampleControllerSpec extends PlaySpec with OneServerPerSuite with Query with MockFactory with BeforeAndAfter {
  val wsClient = app.injector.instanceOf[WSClient]

  val dbConfigProvider = app.injector.instanceOf(classOf[DatabaseConfigProvider])
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  val testKey = Await.result(ApiKey.create(true), Duration.Inf).get.key

  val uuid = java.util.UUID.randomUUID.toString
  val user = Await.result(User.create(
    java.util.UUID.randomUUID.toString, s"${uuid}@uuid.com", uuid, 1
  ), Duration.Inf).get

  val payload = "your binary here".getBytes
  lazy val created = Await.result(
    wsClient
      .url(s"http://localhost:${port}/v1/sample")
      .withHeaders("Chirp-Api-Key" -> testKey)
      .withQueryString(
        "user_id" -> user.id.toString,
        "file_name" -> "scala_test.wav"
      ).put(payload), Duration.Inf)

  "PUT /v1/sample/create" should {
    "create a new sample" in {
      created.status must equal(201)
    }
  }

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
}
