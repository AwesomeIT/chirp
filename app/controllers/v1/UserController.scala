package controllers.v1

import com.google.inject.Inject
import scala.concurrent._
import akka.actor.ActorSystem

import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._

import org.birdfeed.chirp.database.Query.User

@Singleton
class UserController @Inject() (actorSystem: ActorSystem)(implicit exec: ExecutionContext) extends Controller {

  def create = Action.async(BodyParsers.parse.json) { request =>
    val createReads: Reads[Future[Option[User.User]]] = (
      (JsPath \ "name").read[String] and
        (JsPath \ "email").read[String] and
        (JsPath \ "password").read[String]
      )((name: String, email: String, password: String) => {
      User.create(name, email, password, 2)
    })

    request.body.validate(createReads).fold(
      errors => {
        Future {
          BadRequest(Json.obj("error" -> "Something went wrong. We're sorry about that!"))
        }
      },
      user => {
        user.flatMap {
          case Some(user) => Future { Ok(user.jsonWrites.writes(user)) }
          case None => Future { BadRequest(Json.obj("error" -> "Something else went wrong")) }
        }
      }
    )
  }

  def retrieve(id: String) = Action.async(BodyParsers.parse.json) { request =>
    User.find(id.toInt).flatMap {
      case Some(user) => Future { Ok(user.jsonWrites.writes(user)) }
      case None => Future { BadRequest(Json.obj("error" -> s"User with id ${id} not found")) }
    }
  }
}