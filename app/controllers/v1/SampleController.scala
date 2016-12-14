package controllers.v1

import javax.inject.Inject

import akka.actor.ActorSystem
import be.objectify.deadbolt.scala.models.PatternType
import be.objectify.deadbolt.scala.{ActionBuilders, AuthenticatedRequest}
import com.amazonaws.services.s3.model.ObjectMetadata
import com.github.aselab.activerecord.dsl._
import com.google.inject._
import org.birdfeed.chirp.actions.ActionWithValidApiKey
import org.birdfeed.chirp.adapter.S3
import org.birdfeed.chirp.database.models.Sample
import org.birdfeed.chirp.errors.JsonError
import org.joda.time.DateTime
import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods._
import play.api.mvc._

import scala.concurrent._


@Singleton
class SampleController @Inject()(actorSystem: ActorSystem, actionBuilder: ActionBuilders)
                                (implicit exec: ExecutionContext) extends Controller with S3 with JsonError {

  def create(sampleName: String, fileName: String) = ActionWithValidApiKey {
    actionBuilder.PatternAction("sample.write", PatternType.EQUALITY).defaultHandler()(parse.raw) { request =>

      request.body.asBytes(request.body.size).map(_.toArray) match {
        case Some(bytes) => {
          val meta = new ObjectMetadata
          meta.setContentLength(bytes.length)

          Future {
            Created(Sample(sampleName, request.subject.map(_.identifier.toLong).get, bucket.putObject(
              fileName, bytes, meta
            ).key).create.toJson)
          }
        }
        case None => Future { BadRequest(jsonError("Could not buffer audio to S3")) }
      }
    }
  }

  def list = ActionWithValidApiKey {
    actionBuilder.PatternAction("sample", PatternType.EQUALITY).defaultHandler() { request =>
      Future {
        Ok(sampleScope(request).toJson(
          "id", "name", "updatedAt", "createdAt"
        ))
      }
    }
  }

  def retrieve(id: String) = ActionWithValidApiKey {
    actionBuilder.PatternAction("sample", PatternType.EQUALITY).defaultHandler() { request =>
      Sample.find(id.toInt) match {
        case Some(sample) => Future {

          val json = ("id" -> sample.id) ~
            ("name" -> sample.name) ~
            ("updatedAt" -> sample.updatedAt.toString) ~
            ("createdAt" -> sample.createdAt.toString) ~
            ("s3Url" -> bucket
              .get(sample.s3Url)
              .map(_.generatePresignedUrl(DateTime.now.plusHours(2)))
              .map(_.toString)
              .getOrElse("(could not generate pre-signed URL)")
            ) ~ (
              "experiments" -> sample.experiments.toList.map { experiment =>
                s"${request.host}/v1/experiment/${experiment.id}"
              }
            )

          Ok(pretty(json))
        }
        case None => Future { NotFound }
      }
    }
  }

  def delete(id: String) = ActionWithValidApiKey {
    actionBuilder.PatternAction("sample.write", PatternType.EQUALITY).defaultHandler() { request =>
      Sample.find(id.toInt) match {
        case Some(score) => Future { if (score.delete) NoContent else NotFound }
        case None => Future { NotFound }
      }
    }
  }

  // TODO: This is disgusting
  private def sampleScope(request: AuthenticatedRequest[AnyContent]) = {
    if (request.subject.map(_.roles).map(_.map(_.name)).map(_.contains("Researcher")).getOrElse(true)) {
      Sample.where(_.userId === request.subject.map(_.identifier.toLong))
    } else { Sample.all }
  }
}