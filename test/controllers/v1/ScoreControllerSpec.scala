package controllers.v1

import org.birdfeed.chirp.database.models.{ApiKey, Experiment, Sample, User}
import org.birdfeed.chirp.test.BaseSpec
import play.api.libs.json._
import play.api.libs.ws.WSClient

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class ScoreControllerSpec extends BaseSpec {
  val wsClient = app.injector.instanceOf[WSClient]
  val testKey = ApiKey(true).create.key

  val experiment = Experiment(java.util.UUID.randomUUID.toString).create

  val uuid = java.util.UUID.randomUUID.toString
  val user = User(
    java.util.UUID.randomUUID.toString, s"${uuid}@uuid.com", uuid, 1
  ).create

  val sample = Sample(
    "test", user.id, "moo.wav"
  ).create

//  "PUT /v1/score" should {
//    val created = Await.result(
//      wsClient
//        .url(s"http://localhost:${port}/v1/score")
//        .withHeaders("Chirp-Api-Key" -> testKey)
//        .put(Json.obj(
//          "score" -> 2.5,
//          "sample_id" -> sample.id,
//          "experiment_id" -> experiment.id,
//          "user_id" -> user.id
//        )), Duration.Inf)
//
//    "create a new score" in { created.status must equal(201) }
//
//    "retrieve a created score" in {
//      val retrieved = Await.result(
//        wsClient
//          .url(s"http://localhost:${port}/v1/score/${(created.json \ "id").get}")
//          .withHeaders("Chirp-Api-Key" -> testKey)
//          .get, Duration.Inf
//      )
//
//      retrieved.status must equal(200)
//    }
//  }
}
