package controllers.v1

import javax.inject.Inject

import akka.actor.ActorSystem
import be.objectify.deadbolt.scala.ActionBuilders
import be.objectify.deadbolt.scala._
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
    actionBuilder.RestrictAction("sample", "sample.write").defaultHandler() { authenticatedRequest =>
      val audioBytes = for {
        buf <- authenticatedRequest.body.asRaw
        byteString <- buf.asBytes(buf.size)
      } yield byteString.toArray


      audioBytes match {
        case Some(byteArray) => {
          val meta = new ObjectMetadata
          meta.setContentLength(byteArray.length)

          Future {
            Created(Sample(fileName, userId, bucket.putObject(
              fileName, byteArray, meta
            ).key).create.toJson)
          }
        }
        case None => Future {
          BadRequest(jsonError("Unable to read audio buffer. Please try again."))
        }
      }
    }
  }

  def retrieve(id: String) = ActionWithValidApiKey {
    actionBuilder.RestrictAction("sample").defaultHandler() { authenticatedRequest =>
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
    actionBuilder.RestrictAction("sample", "sample.write").defaultHandler() { authenticatedRequest =>
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