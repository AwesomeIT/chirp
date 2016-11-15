package controllers.v1

import akka.actor.ActorSystem
import com.amazonaws.services.s3.model.ObjectMetadata
import com.google.inject._
import org.birdfeed.chirp.database.Query
import org.birdfeed.chirp.adapter.S3
import org.birdfeed.chirp.actions.EndpointHandler
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc._
import slick.driver.JdbcProfile

import scala.concurrent._

@Singleton
class SampleController @Inject()(actorSystem: ActorSystem, val dbConfigProvider: DatabaseConfigProvider)(
  implicit exec: ExecutionContext
) extends Controller with EndpointHandler with Query with S3 {

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  def create(fileName: String, userId: Int) = Action.async(parse.raw) { request =>
    request.body.asBytes(request.body.size) match {
      case Some(bytes) => {
        val meta = new ObjectMetadata
        meta.setContentLength(request.body.size)

        dtoWithErrorHandlingSingle(
          Sample.create(
            fileName,
            userId,
            bucket.putObject(fileName, bytes.toArray, meta).key
          ), Created)
      }
      case None => Future(InternalServerError(jsonError("S3 file upload failed. Please try again")))
    }
  }

  def retrieve(id: String) = Action.async { request =>
    dtoWithErrorHandlingSingle(Sample.find(id.toInt), Ok)
  }

  def delete(id: String) = Action.async { request =>
    anyWithErrorHandlingSingle(Sample.delete(id.toInt), Ok)
  }
}