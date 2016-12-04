package controllers.v1

import javax.inject.Inject

import akka.actor.ActorSystem
import be.objectify.deadbolt.scala.ActionBuilders
import be.objectify.deadbolt.scala.models.PatternType
import com.google.inject._
import org.birdfeed.chirp.actions.ActionWithValidApiKey
import org.birdfeed.chirp.database.models.Experiment
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent._

@Singleton
class ExperimentController @Inject()(actorSystem: ActorSystem, actionBuilder: ActionBuilders)(implicit exec: ExecutionContext) extends Controller {

  def create = ActionWithValidApiKey {
    actionBuilder.PatternAction("experiment.write", PatternType.EQUALITY).defaultHandler()(BodyParsers.parse.json) { authenticatedRequest =>
      implicit val createReads: Reads[Experiment] = (
        (JsPath \ "name").read[String] and
        (JsPath \ "user_id").read[Long]
      )(Experiment.apply _)

      authenticatedRequest.body.validate.map { experiment =>
        Future { Created(experiment.create.toJson) } }.get
    }
  }

  def retrieve(id: String) = ActionWithValidApiKey {
    actionBuilder.PatternAction("experiment", PatternType.EQUALITY).defaultHandler() {
      Experiment.find(id.toInt) match {
        case Some(experiment) => Future { Ok(experiment.toJson) }
        case None => Future { NotFound }
      }
    }
  }

  def delete(id: String) = ActionWithValidApiKey {
    actionBuilder.PatternAction("experiment.write", PatternType.EQUALITY).defaultHandler() {
      Experiment.find(id.toInt) match {
        case Some(experiment) => {
          if (experiment.delete) Future { NoContent } else Future { NotFound }
        }
        case None => Future { NotFound }
      }
    }
  }
//
//  def getSamples(id: String) = ActionWithValidApiKey(dbConfigProvider) {
//    Action.async {
//      SampleExperiment.where(_.experimentId === id.toInt).map { sample_experiments =>
//        val serialized = JsArray(
//          sample_experiments.get.map { se => se.jsonWrites.writes(se.asInstanceOf[se.type]) }
//        )
//        Ok(serialized)
//      }
//    }
//  }

  /*def update(id: String) = Action.async(BodyParsers.parse.json) { request =>
    val updateReads: Reads[Future[Try[Experiment]]] = (
      (JsPath \ "name").readNullable[String] and
        (JsPath \ "startDate").readNullable[String] and
        (JsPath \ "endDate").readNullable[String]
      ) ((expName: Option[String], startDate: Option[String], endDate: Option[String]) => {
      Experiment.find(id.toInt).flatMap {
        case Success(retrieved) => {
          val format = new SimpleDateFormat("MMddYYYY")
          val sqlStartDate = new java.sql.Date(format.parse(startDate.get).getTime)
          val (updatedName, updatedStartDate, updatedEndDate) = (
            expName.getOrElse(retrieved.name),
            if (startDate != null) new java.sql.Date(format.parse(startDate.get).getTime) else retrieved.startDate,
            if (endDate != null) new java.sql.Date(format.parse(endDate.get).getTime) else retrieved.endDate
            )

          val updatedRow = retrieved.slickTE.copy(
            retrieved.id,
            updatedName,
            updatedStartDate,
            updatedEndDate.asInstanceOf[Option[Date]],
            retrieved.createdAt,
            retrieved.updatedAt
          )

          Future {
            Experiment.updateById(id.toInt, updatedRow)
          }
        }
      }
    })

    dtoWithMarshallingSingle(updateReads, request.body, Ok)
  }*/
}