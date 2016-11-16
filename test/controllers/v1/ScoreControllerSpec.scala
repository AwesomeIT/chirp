package controllers.v1

import org.birdfeed.chirp.database.Query
import org.scalatestplus.play._
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json._
import play.api.libs.ws.WSClient
import slick.driver.JdbcProfile

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class ScoreControllerSpec extends PlaySpec with OneServerPerSuite with Query {
  val wsClient = app.injector.instanceOf[WSClient]

  val dbConfigProvider = app.injector.instanceOf(classOf[DatabaseConfigProvider])
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  val testKey = Await.result(ApiKey.create(true), Duration.Inf).get.key

  val experiment = Await.result(
    Experiment.create(
      java.util.UUID.randomUUID.toString, new java.sql.Date(java.util.Calendar.getInstance.getTime.getTime),
      Some(new java.sql.Date(java.util.Calendar.getInstance.getTime.getTime))), Duration.Inf).get

  val uuid = java.util.UUID.randomUUID.toString
  val user = Await.result(User.create(
    java.util.UUID.randomUUID.toString, s"${uuid}@uuid.com", uuid, 1
  ), Duration.Inf).get

  val sample = Await.result(Sample.create(
    "test", user.id, "moo.wav"
  ), Duration.Inf).get

  "PUT /v1/score" should {
    lazy val created = Await.result(
      wsClient
        .url(s"http://localhost:${port}/v1/score")
        .withHeaders("Chirp-Api-Key" -> testKey)
        .put(Json.obj(
          "score" -> 2.5,
          "sample_id" -> sample.id,
          "experiment_id" -> experiment.id,
          "user_id" -> user.id
        )), Duration.Inf)

    "create a new score" in { created.status must equal(201) }

    "retrieve a created score" in {
      lazy val retrieved = Await.result(
        wsClient
          .url(s"http://localhost:${port}/v1/score/${(created.json \ "id").get}")
          .withHeaders("Chirp-Api-Key" -> testKey)
          .get, Duration.Inf
      )

      retrieved.status must equal(200)
    }
  }
}
