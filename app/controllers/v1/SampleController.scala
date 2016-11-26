package controllers.v1

import javax.inject.Inject

import akka.actor.ActorSystem
import com.amazonaws.services.s3.model.ObjectMetadata
import com.google.inject._
import org.birdfeed.chirp.actions.ActionWithValidApiKey
import org.birdfeed.chirp.adapter.S3
import org.birdfeed.chirp.database.models.Sample
import org.birdfeed.chirp.errors.JsonError
import play.api.mvc._

import scala.concurrent._

@Singleton
class SampleController @Inject() (actorSystem: ActorSystem)(implicit exec: ExecutionContext) extends Controller with S3 with JsonError {

  def create(fileName: String, userId: Int) = ActionWithValidApiKey {
    Action.async(parse.raw) { request =>
      request.body.asBytes(request.body.size) match {
        case Some(bytes) => {
          val meta = new ObjectMetadata
          meta.setContentLength(request.body.size)

          Future {
            val sample = Sample(
              fileName,
              userId,
              bucket.putObject(fileName, bytes.toArray, meta).key
            ).create

            Created(sample.toJson)
          }
        }
        case None => Future(InternalServerError(jsonError("S3 file upload failed. Please try again")))
      }
    }
  }

  def retrieve(id: String) = ActionWithValidApiKey {
    Action.async { request =>
      Sample.find(id.toInt) match {
        case Some(score) => Future { Ok(score.toJson) }
        case None => Future { NotFound }
      }
    }
  }

  def delete(id: String) = ActionWithValidApiKey {
    Action.async {
      Sample.find(id.toInt) match {
        case Some(score) => {
          if (score.delete) {
            Future { NoContent }
          } else { Future { NotFound } }
        }
        case None => Future { NotFound }
      }
    }
  }
}