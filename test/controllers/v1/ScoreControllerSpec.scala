package controllers.v1

import org.birdfeed.chirp.database.Query
import org.birdfeed.chirp.support.test.BaseSpec
import org.joda.time.DateTime
import org.scalatest.{AsyncWordSpec, MustMatchers, ParallelTestExecution}
import org.scalatestplus.play._
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json._
import play.api.libs.ws.WSClient
import slick.driver.JdbcProfile

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class ScoreControllerSpec extends BaseSpec {
  val wsClient = app.injector.instanceOf[WSClient]

  val experiment = Await.result(
    Experiment.create(
      java.util.UUID.randomUUID.toString, new java.sql.Date(DateTime.now.getMillis),
      Some(new java.sql.Date(DateTime.now.getMillis))), Duration.Inf).get

  val uuid = java.util.UUID.randomUUID.toString
  val user = Await.result(User.create(
    java.util.UUID.randomUUID.toString, s"${uuid}@uuid.com", uuid, 1
  ), Duration.Inf).get

  val sample = Await.result(Sample.create(
    "test", user.id, "moo.wav"
  ), Duration.Inf).get

  lazy val created = Await.result(
    wsClient
      .url(s"http://localhost:${port}/v1/score")
      .put(Json.obj(
        "score" -> 2.5,
        "sample_id" -> sample.id,
        "experiment_id" -> experiment.id,
        "user_id" -> user.id
      )), Duration.Inf
  )

  "PUT /v1/score" should {
    "create a new score" in {
      wsClient
        .url(s"http://localhost:${port}/v1/score")
        .put(Json.obj(
          "score" -> 2.5,
          "sample_id" -> sample.id,
          "experiment_id" -> experiment.id,
          "user_id" -> user.id
        )).map { response => response.status must equal(201) }
    }
  }

  "GET /v1/score" should {
    "retrieve a created score" in {
      wsClient
        .url(s"http://localhost:${port}/v1/score/${(created.json \ "id").get}")
        .get.map { response => response.status must equal(200) }
    }
  }
}
