package controllers.v1

import akka.actor.ActorSystem
import com.amazonaws.services.s3.model.ObjectMetadata
import com.google.inject._
import org.birdfeed.chirp.database.Query
import org.birdfeed.chirp.adapter.S3
import org.birdfeed.chirp.actions.ActionWithValidApiKey
import org.birdfeed.chirp.errors.JsonError
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc._
import slick.driver.JdbcProfile

import scala.concurrent._

@Singleton
class SampleController @Inject()(actorSystem: ActorSystem, val dbConfigProvider: DatabaseConfigProvider)(
  implicit exec: ExecutionContext
) extends Controller with Query with S3 with JsonError {

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  def create(fileName: String, userId: Int) = ActionWithValidApiKey(dbConfigProvider) {
    Action.async(parse.raw) { request =>
      request.body.asBytes(request.body.size) match {
        case Some(bytes) => {
          val meta = new ObjectMetadata
          meta.setContentLength(request.body.size)

          Sample.create(
            fileName,
            userId,
            bucket.putObject(fileName, bytes.toArray, meta).key
          ).map { created =>
            val cGet = created.get
            Created(cGet.jsonWrites.writes(cGet))
          }
        }
        case None => Future(InternalServerError(jsonError("S3 file upload failed. Please try again")))
      }
    }
  }

  def retrieve(id: String) = ActionWithValidApiKey(dbConfigProvider) {
    Action.async { request =>
      Sample.find(id.toInt).map { retrieved =>
        val rGet = retrieved.get
        Ok(rGet.jsonWrites.writes(rGet))
      }
    }
  }

  def delete(id: String) = ActionWithValidApiKey(dbConfigProvider) {
    Action.async { Sample.delete(id.toInt).map { count => Ok(count.get.toString) } }
  }
}