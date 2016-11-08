package controllers.v1

import akka.actor.ActorSystem
import com.google.inject._
import org.birdfeed.chirp.database.Query
import org.birdfeed.chirp.database.models.Sample
import org.birdfeed.chirp.helpers.EndpointHandler
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc._
import slick.driver.JdbcProfile

import scala.concurrent._
import scala.util._

@Singleton
class SampleController @Inject()(actorSystem: ActorSystem, val dbConfigProvider: DatabaseConfigProvider)(implicit exec: ExecutionContext) extends Controller with EndpointHandler with Query {

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  def create = Action.async(BodyParsers.parse.json) { request =>
    val createReads: Reads[Future[Try[Sample]]] = (
        (JsPath \ "name").read[String] and
        (JsPath \ "user_id").read[Int] and
        (JsPath \ "s3_url").read[String]
    )((name: String, userId: Int, s3Url: String) => {
      Sample.create(name, userId, s3Url)
    })

    dtoWithMarshallingSingle(createReads, request.body, Created)
  }

  def retrieve(id: String) = Action.async { request =>
    dtoWithErrorHandlingSingle(Sample.find(id.toInt), Ok)
  }

  def delete(id: String) = Action.async { request =>
    anyWithErrorHandlingSingle(Sample.delete(id.toInt), Ok)
  }
}