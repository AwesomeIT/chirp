package controllers.v1

import org.birdfeed.chirp.database.Query
import org.joda.time.DateTime
import org.scalatestplus.play._
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json._
import play.api.libs.ws.WSClient
import slick.driver.JdbcProfile

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class ExperimentControllerSpec extends PlaySpec with OneServerPerSuite with Query {
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
        "start_date" -> "10102012"
      )), Duration.Inf)


  "PUT /v1/experiment" should {
    "create a new experiment" in {
      created.status must equal(201)
    }
  }

  "GET /v1/experiment/:id" should {
    "retrieve a created experiment" in {
      lazy val retrieved = Await.result(
        wsClient
          .url(s"http://localhost:${port}/v1/experiment/${(created.json \ "id").get}")
          .withHeaders("Chirp-Api-Key" -> testKey)
          .get, Duration.Inf
      ).status must equal(200)
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

  "GET /v1/experiment/:id/samples" should {
    "get a list of samples for an experiment" in {
      Await.result(
        wsClient.url(s"http://localhost:${port}/v1/experiment/${(created.json \ "id").get}/samples")
          .withHeaders("Chirp-Api-Key" -> testKey)
            .get, Duration.Inf
      ).status must equal(200)
    }
  }
}
