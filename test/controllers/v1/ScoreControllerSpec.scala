package controllers.v1

import org.birdfeed.chirp.database.models.{ApiKey, Experiment, Sample, User}
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.libs.json._
import play.api.libs.ws.WSClient

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class ScoreControllerSpec extends PlaySpec with GuiceOneServerPerSuite {
  val wsClient = app.injector.instanceOf[WSClient]
  var testKey = ApiKey(true).create.key

  lazy val experiment = Experiment(java.util.UUID.randomUUID.toString).create

  lazy val uuid = java.util.UUID.randomUUID.toString
  lazy val user = User(
    java.util.UUID.randomUUID.toString, s"${uuid}@uuid.com", uuid, 1
  ).create

  lazy val sample = Sample(
    "test", user.id, "moo.wav"
  ).create

  "PUT /v1/score" should {
    lazy val created = Await.result(
      wsClient
        .url(s"http://localhost:${portNumber.value}/v1/score")
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
          .url(s"http://localhost:${portNumber.value}/v1/score/${(created.json \ "id").get}")
          .withHeaders("Chirp-Api-Key" -> testKey)
          .get, Duration.Inf
      )

      retrieved.status must equal(200)
    }
  }
}
