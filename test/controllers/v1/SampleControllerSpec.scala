package controllers.v1

import org.scalatestplus.play._
import play.api.libs.json._
import play.api.libs.ws.WSClient
import play.api.db.slick.DatabaseConfigProvider

import slick.driver.JdbcProfile

import scala.concurrent.Await
import scala.concurrent.duration.Duration

import org.birdfeed.chirp.database.Query

class SampleControllerSpec extends PlaySpec with OneServerPerSuite with Query {
  val wsClient = app.injector.instanceOf[WSClient]

  val dbConfigProvider = app.injector.instanceOf(classOf[DatabaseConfigProvider])
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  val uuid = java.util.UUID.randomUUID.toString
  val user = Await.result(User.create(
    java.util.UUID.randomUUID.toString, s"${uuid}@uuid.com", uuid, 1
  ), Duration.Inf).get

  "PUT /v1/sample/create" should {
    lazy val created = Await.result(
      wsClient
        .url(s"http://localhost:${port}/v1/sample")
        .put(Json.obj(
          "name" -> "testSound",
          "user_id" -> user.id,
          "s3_url" -> "woof.mp3"
        )), Duration.Inf)

    "create a new sample" in { created.status must equal(201) }

    "retrieve a created sample" in {
      lazy val retrieved = Await.result(
        wsClient
          .url(s"http://localhost:${port}/v1/sample/${(created.json \ "id").get}")
          .get, Duration.Inf
      )

      retrieved.status must equal(200)
    }

    "delete a created sample" in {
      Await.result(
        wsClient.url(s"http://localhost:${port}/v1/sample/${(created.json \ "id").get}").delete,
        Duration.Inf
      ).body.toInt must equal(1)
    }
  }
}
