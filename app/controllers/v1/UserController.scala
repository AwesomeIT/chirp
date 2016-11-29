package controllers.v1

import akka.actor.ActorSystem
import com.google.inject._
import org.birdfeed.chirp.actions.ActionWithValidApiKey
import org.birdfeed.chirp.database.models.User
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent._
import com.github.t3hnar.bcrypt._


@Singleton
class UserController @Inject()(actorSystem: ActorSystem)(implicit exec: ExecutionContext) extends Controller {


  def authenticate = ActionWithValidApiKey {
    Action.async(BodyParsers.parse.json) { request =>
      implicit val authenticateReads: Reads[Option[Result]] = (
        (JsPath \ "email").read[String] and
        (JsPath \ "password").read[String]
      )((email: String, password: String) => {
        User.authenticate(email, password)
          .map { token => Ok(token.toJson) }
      })

      request.body.validate.get match {
        case Some(result) => Future { result }
        case None => Future { Unauthorized }
      }
    }
  }

  def create = ActionWithValidApiKey {
    Action.async(BodyParsers.parse.json) { request =>
      implicit val createReads: Reads[Future[Result]] = (
        (JsPath \ "name").read[String] and
        (JsPath \ "email").read[String] and
        (JsPath \ "password").read[String]
      )((name: String, email: String, password: String) => {
        Future { Created(
          User(name, email, password, 2).create.toJson("id", "name", "email")
        )}
      })

      request.body.validate(createReads).get
    }
  }

  def retrieve(id: String) = ActionWithValidApiKey {
    Action.async {
      User.find(id.toInt) match {
        case Some(user) => Future { Ok(user.toJson("id", "name", "email")) }
        case None => Future { NotFound }
      }
    }
  }

  def delete(id: String) = ActionWithValidApiKey {
    Action.async {
      User.find(id.toInt) match {
        case Some(user) => if (user.delete) {
          Future(NoContent)
        } else { Future(BadRequest) }
        case None => Future(NotFound)
      }
    }
  }
}