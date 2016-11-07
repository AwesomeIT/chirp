package controllers.v1

import java.text.SimpleDateFormat

import com.google.inject._

import scala.util._
import scala.concurrent._
import akka.actor.ActorSystem
import org.birdfeed.chirp.database.{Query, Tables}
import org.birdfeed.chirp.database.models.Experiment
import org.birdfeed.chirp.helpers.EndpointHandler
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

@Singleton
class ExperimentController @Inject() (actorSystem: ActorSystem, val dbConfigProvider: DatabaseConfigProvider)(implicit exec: ExecutionContext) extends Controller with EndpointHandler with Query {

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  def create = Action.async(BodyParsers.parse.json) { request =>
    val createReads: Reads[Future[Try[Experiment]]] = (
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

    dtoWithMarshallingSingle(createReads, request.body, Created)
  }

  def retrieve(id: String) = Action.async(BodyParsers.parse.json) { request =>
    dtoWithErrorHandlingSingle(Experiment.find(id.toInt), Ok)
  }

  def delete(id: String) = Action.async { request =>
    anyWithErrorHandlingSingle(Experiment.delete(id.toInt), Ok)
  }

  def update(id: String, row: Tables.Experiment#TableElementType) = Action.async { request =>
    anyWithErrorHandlingSingle(Experiment.updateById(id.toInt, row), Ok)
  }

}