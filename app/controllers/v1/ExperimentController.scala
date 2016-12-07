package controllers.v1

import javax.inject.Inject

import akka.actor.ActorSystem
import be.objectify.deadbolt.scala.ActionBuilders
import be.objectify.deadbolt.scala.models.PatternType
import com.github.aselab.activerecord.dsl._
import com.google.inject._
import org.birdfeed.chirp.actions.ActionWithValidApiKey
import org.birdfeed.chirp.database.models.Experiment
import org.json4s.JsonDSL._
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc.{BodyParsers, Controller}

import scala.concurrent._

@Singleton
class ExperimentController @Inject()(actorSystem: ActorSystem, actionBuilder: ActionBuilders)
                                    (implicit exec: ExecutionContext) extends Controller {

  def create = ActionWithValidApiKey {
    actionBuilder.PatternAction("experiment.write", PatternType.EQUALITY).defaultHandler()(BodyParsers.parse.json) { request =>
      implicit val createReads: Reads[Experiment] = (
        (JsPath \ "name").read[String] and
        (JsPath \ "user_id").read[Long]
      )(Experiment.apply _)

      request.body.validate.map { experiment =>
        Future { Created(experiment.create.toJson) } }.get
    }
  }

  def retrieve(id: String) = ActionWithValidApiKey {
    actionBuilder.PatternAction("experiment", PatternType.EQUALITY).defaultHandler() { request =>
      Experiment.find(id.toInt) match {
        case Some(experiment) => Future {
          val json = experiment.asJson ++ (
            "samples" -> experiment.samples.toList.map(_.asJson)
          )
          Ok(org.json4s.jackson.JsonMethods.pretty(json))
        }
        case None => Future { NotFound }
      }
    }
  }

  def delete(id: String) = ActionWithValidApiKey {
    actionBuilder.PatternAction("experiment.write", PatternType.EQUALITY).defaultHandler() {
      Experiment.find(id.toInt) match {
        case Some(experiment) => Future { if (experiment.delete) NoContent else NotFound }
        case None => Future { NotFound }
      }
    }
  }
}