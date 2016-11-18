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

class ExperimentControllerSpec extends PlaySpec with OneServerPerSuite with Query with MockFactory with BeforeAndAfter {
  val wsClient = app.injector.instanceOf[WSClient]

  val dbConfigProvider = app.injector.instanceOf(classOf[DatabaseConfigProvider])
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  val testKey = Await.result(ApiKey.create(true), Duration.Inf).get.key
  lazy val created = Await.result(
    wsClient
      .url(s"http://localhost:${port}/v1/experiment")
      .withHeaders("Chirp-Api-Key" -> testKey)
      .put(Json.obj(
        "name" -> "name",
        "start_date" -> new java.sql.Date(java.util.Calendar.getInstance.getTime.getTime)
      )), Duration.Inf)

  "PUT /v1/experiment/create" should {
    "create a new experiment" in {
      created.status must equal(201)
    }
  }

  "GET /v1/experiment/:id" should {
    "retrieve a created experiment" in {
      lazy val retrieved = Await.result(
        wsClient
          .url(s"http://localhost:${port}/v1/sample/${(created.json \ "id").get}")
          .withHeaders("Chirp-Api-Key" -> testKey)
          .get, Duration.Inf
      )

      retrieved.status must equal(200)
    }
  }

  "DELETE /v1/experiment/:id" should {
    "delete a created experiment" in {
      Await.result(
        wsClient.url(s"http://localhost:${port}/v1/experiment/${(created.json \ "id").get}")
          .withHeaders("Chirp-Api-Key" -> testKey)
          .delete,
        Duration.Inf
      ).body.toInt must equal(1)
    }
  }

}
