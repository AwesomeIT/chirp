package controllers.v1

import javax.inject.Inject

import akka.actor.ActorSystem
import com.google.inject._
import org.birdfeed.chirp.actions.ActionWithValidApiKey
import org.birdfeed.chirp.database.models.Score
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent._

@Singleton
class ScoreController @Inject() (actorSystem: ActorSystem)(implicit exec: ExecutionContext) extends Controller {
  def create = ActionWithValidApiKey {
    Action.async(BodyParsers.parse.json) { request =>
      implicit val createReads: Reads[Score] = (
        (JsPath \ "score").read[Double] and
        (JsPath \ "sample_id").read[Long] and
        (JsPath \ "experiment_id").read[Long] and
        (JsPath \ "user_id").read[Long]
      )(Score.apply _)

      request.body.validate.map { score => Future { Created(score.toJson) } }.get
    }
  }

  def retrieve(id: String) = ActionWithValidApiKey {
    Action.async {
      Score.find(id.toInt) match {
        case Some(score) => Future { Ok(score.toJson) }
        case None => Future { NotFound }
      }
    }
  }
}