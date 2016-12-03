package controllers.v1

import javax.inject.Inject

import akka.actor.ActorSystem
import be.objectify.deadbolt.scala.ActionBuilders
import be.objectify.deadbolt.scala.models.PatternType
import com.amazonaws.services.s3.model.ObjectMetadata
import com.google.inject._
import org.birdfeed.chirp.actions.ActionWithValidApiKey
import org.birdfeed.chirp.adapter.S3
import org.birdfeed.chirp.database.models.Sample
import org.birdfeed.chirp.errors.JsonError
import play.api.mvc._

import scala.concurrent._

@Singleton
class SampleController @Inject()(actorSystem: ActorSystem, actionBuilder: ActionBuilders)(implicit exec: ExecutionContext) extends Controller with S3 with JsonError {

  def create(fileName: String, userId: Int) = ActionWithValidApiKey {
    actionBuilder.PatternAction("sample.write", PatternType.EQUALITY).defaultHandler()(parse.raw) { authenticatedRequest =>

      authenticatedRequest.body.asBytes(authenticatedRequest.body.size).map(_.toArray) match {
        case Some(bytes) => {
          val meta = new ObjectMetadata
          meta.setContentLength(bytes.length)

          Future {
            Created(Sample(fileName, userId, bucket.putObject(
              fileName, bytes, meta
            ).key).create.toJson)
          }
        }
        case None => {
          Future {
            BadRequest(jsonError("Could not buffer audio to S3"))
          }
        }
      }
    }
  }

  def retrieve(id: String) = ActionWithValidApiKey {
    actionBuilder.PatternAction("sample", PatternType.EQUALITY).defaultHandler() { authenticatedRequest =>
      Sample.find(id.toInt) match {
        case Some(score) => Future {
          Ok(score.toJson)
        }
        case None => Future {
          NotFound
        }
      }
    }
  }

  def delete(id: String) = ActionWithValidApiKey {
    actionBuilder.PatternAction("sample.write", PatternType.EQUALITY).defaultHandler() { authenticatedRequest =>
      Sample.find(id.toInt) match {
        case Some(score) => {
          if (score.delete) {
            Future {
              NoContent
            }
          } else {
            Future {
              NotFound
            }
          }
        }
        case None => Future {
          NotFound
        }
      }
    }
  }
}