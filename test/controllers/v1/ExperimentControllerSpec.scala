package controllers.v1

import org.birdfeed.chirp.database.models.User
import org.birdfeed.chirp.test.BaseSpec
import play.api.libs.json._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class ExperimentControllerSpec extends BaseSpec {
  val uuid = java.util.UUID.randomUUID.toString
  lazy val user = User(
    java.util.UUID.randomUUID.toString, s"${uuid}@uuid.com", uuid, 1
  ).create

  lazy val created = Await.result(
    wsClient
      .url(s"http://localhost:${port}/v1/experiment")
      .withHeaders("Chirp-Api-Key" -> testKey)
      .put(Json.obj(
        "name" -> "name",
        "user_id" -> user.id
      )), Duration.Inf)


  "PUT /v1/experiment" should {
    "create a new experiment" in {
      created.status must equal(201)
    }
  }

  "GET /v1/experiment/:id" should {
    "retrieve a created experiment" in {
      Await.result(
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
      ).status must equal(204)
    }
  }

//  "GET /v1/experiment/:id/samples" should {
//    "get a list of samples for an experiment" in {
//      Await.result(
//        wsClient.url(s"http://localhost:${port}/v1/experiment/${(created.json \ "id").get}/samples")
//          .withHeaders("Chirp-Api-Key" -> testKey)
//            .get, Duration.Inf
//      ).status must equal(200)
//    }
//  }
}
