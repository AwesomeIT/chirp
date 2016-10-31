package controllers.v1

import java.sql.Date
import java.text.SimpleDateFormat

import com.google.inject._

import scala.util._
import scala.concurrent._
import akka.actor.ActorSystem
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import org.birdfeed.chirp.database.Query.Experiment
import org.postgresql.util.PSQLException
import play.api.data.format

@Singleton
class ExperimentController @Inject() (actorSystem: ActorSystem)(implicit exec: ExecutionContext) extends Controller {

  def create = Action.async(BodyParsers.parse.json) { request =>
    val createReads: Reads[Future[Option[Experiment.Experiment]]] = (
      (JsPath \ "name").read[String] and
        (JsPath \ "startDate").read[String] and
        (JsPath \ "endDate").readNullable[String]
      )((name: String, startDate: String, endDate: Option[String]) => {
        val format = new SimpleDateFormat("MMddYYYY")
        val sqlStartDate = new java.sql.Date(format.parse(startDate).getTime)
        Experiment.create(name, sqlStartDate, endDate.map { date =>
          new java.sql.Date(format.parse(date).getTime)
        })
    })

    request.body.validate(createReads).fold(
      errors => {
        Future {
          BadRequest(Json.obj("error" -> "Something went wrong. We're sorry about that!"))
        }
      },
      experiment => {
        experiment.flatMap {
          case Some(experiment) => Future { Ok(experiment.jsonWrites.writes(experiment)) }
          case None => Future { BadRequest(Json.obj("error" -> "Something else went wrong")) }
        }
      }
    )
  }

  def retrieve(id: String) = Action.async { request =>
    Experiment.find(id.toInt).flatMap {
      case Some(experiment) => Future { Ok(experiment.jsonWrites.writes(experiment)) }
      case None => Future { BadRequest(Json.obj("error" -> s"Experiment with id ${id} not found")) }
    }
  }

  def delete(id: String) = Action.async(BodyParsers.parse.json) { request =>
    Experiment.delete(id.toInt) match {
      case Success(id) => Future { Ok(s"Experiment with id ${id.toString} was deleted") }
      case Failure(t: PSQLException) => Future { BadRequest(Json.obj("error" -> s"Experiment could not be deleted")) }
    }
  }
}