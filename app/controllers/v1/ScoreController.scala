package controllers.v1

import com.google.inject._

import scala.concurrent._
import akka.actor.ActorSystem
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import org.birdfeed.chirp.database.{Query, Relation}
import org.birdfeed.chirp.database.models.{Score, User}
import org.birdfeed.chirp.helpers.EndpointHandler
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import scala.util._

@Singleton
class ScoreController @Inject()(actorSystem: ActorSystem, val dbConfigProvider: DatabaseConfigProvider)(implicit exec: ExecutionContext) extends Controller with EndpointHandler with Query {

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  def create = Action.async(BodyParsers.parse.json) { request =>
    val createReads: Reads[Future[Try[Score]]] = (
      (JsPath \ "score").read[BigDecimal] and
        (JsPath \ "sample_id").read[Int] and
        (JsPath \ "experiment_id").read[Int] and
        (JsPath \ "user_id").read[Int]
      )((score: BigDecimal, sampleId: Int, experimentId: Int, userId: Int) => {
      Score.create(score, sampleId, experimentId, userId)
    })

    dtoWithMarshallingSingle(createReads, request.body, Created)
  }

  def retrieve(id: String) = Action.async { request =>
    dtoWithErrorHandlingSingle(Score.find(id.toInt), Ok)
  }
}