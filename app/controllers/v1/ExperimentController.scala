package controllers.v1

import javax.inject.Inject

import akka.actor.ActorSystem
import be.objectify.deadbolt.scala.{ActionBuilders, AuthenticatedRequest}
import be.objectify.deadbolt.scala.models.PatternType
import com.github.aselab.activerecord.dsl._
import com.google.inject._
import org.birdfeed.chirp.actions.ActionWithValidApiKey
import org.birdfeed.chirp.database.models.{Experiment, Sample}
import org.json4s.JsonDSL._
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc.{AnyContent, BodyParsers, Controller}
import org.json4s.jackson.JsonMethods._

import scala.concurrent._

@Singleton
class ExperimentController @Inject()(actorSystem: ActorSystem, actionBuilder: ActionBuilders)
                                    (implicit exec: ExecutionContext) extends Controller {

  def create = ActionWithValidApiKey {
    actionBuilder.PatternAction("experiment.write", PatternType.EQUALITY).defaultHandler()(BodyParsers.parse.json) { request =>
      implicit val createReads: Reads[Experiment] = (
        (JsPath \ "name").read[String] and
        (JsPath \ "userId").read[Long]
      )(Experiment.apply _)

      request.body.validate.map { experiment =>
        Future { Created(experiment.create.toJson) } }.get
    }
  }

  def associate(id: String) = ActionWithValidApiKey {
    actionBuilder.PatternAction("experiment.write", PatternType.EQUALITY).defaultHandler()(BodyParsers.parse.json) { request =>
      val sampleIds = (request.body \ "samples").as[List[Int]]

      experimentScope(request).find(id.toLong) match {
        case Some(experiment) => Future {
          experiment.samples.removeAll
          experiment.samples := Sample.where(_.id in sampleIds)

          Ok(pretty(
            ("id" -> experiment.id) ~
              ("name" -> experiment.name) ~
              ("updatedAt" -> experiment.updatedAt.toString) ~
              ("createdAt" -> experiment.createdAt.toString) ~
              ("samples" -> experiment.samples.toList.map(_.asJson("id", "name", "updatedAt", "createdAt")))))
        }
        case None => Future { NotFound }
      }
    }
  }

  def list = ActionWithValidApiKey {
    actionBuilder.PatternAction("experiment", PatternType.EQUALITY).defaultHandler() { request =>
      Future {
        Ok(experimentScope(request).toJson(
          "id", "name", "updatedAt", "createdAt"
        ))
      }
    }
  }

  def retrieve(id: String) = ActionWithValidApiKey {
    actionBuilder.PatternAction("experiment", PatternType.EQUALITY).defaultHandler() { request =>
       experimentScope(request).find(id.toLong) match {
        case Some(experiment) => Future {
          Ok(pretty(
            ("id" -> experiment.id) ~
              ("name" -> experiment.name) ~
              ("updatedAt" -> experiment.updatedAt.toString) ~
              ("createdAt" -> experiment.createdAt.toString) ~
              ("samples" -> experiment.samples.toList.map(_.asJson("id", "name", "updatedAt", "createdAt")))
          ))
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

  // TODO: This is disgusting
  private def experimentScope(request: AuthenticatedRequest[Any]) = {
    if (request.subject.map(_.roles).map(_.map(_.name)).get.contains("Researcher")) {
      Experiment.where(_.userId === request.subject.map(_.identifier.toLong))
    } else { Experiment.all }
  }
}