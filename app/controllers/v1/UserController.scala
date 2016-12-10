package controllers.v1

import akka.actor.ActorSystem
import be.objectify.deadbolt.scala.ActionBuilders
import be.objectify.deadbolt.scala.models.PatternType
import com.google.inject._
import org.birdfeed.chirp.actions.ActionWithValidApiKey
import org.birdfeed.chirp.database.models.{AccessToken, User}
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent._


@Singleton
class UserController @Inject()(actorSystem: ActorSystem, actionBuilder: ActionBuilders)(implicit exec: ExecutionContext) extends Controller {

  def authenticate = ActionWithValidApiKey {
    Action.async(BodyParsers.parse.json) { request =>
      implicit val authenticateReads: Reads[Option[Result]] = (
        (JsPath \ "email").read[String] and
        (JsPath \ "password").read[String]
      )((email: String, password: String) => {
        User.authenticate(email, password)
          .map { token => Ok(token.toJson("userId", "token", "refreshToken", "roleId")) }
      })

      request.body.validate.get match {
        case Some(result) => Future { result }
        case None => Future { Unauthorized }
      }
    }
  }

  def refresh = ActionWithValidApiKey {
    Action.async(BodyParsers.parse.json) { request =>
      implicit val refreshReads: Reads[Option[AccessToken]] =
        (JsPath \ "refreshToken").read[String].map(User.refresh)

      request.body.validate.get match {
        case Some(token) => Future { Ok(token.toJson("userId", "token", "refreshToken")) }
        case None => Future { Unauthorized }
      }
    }
  }

  def create = ActionWithValidApiKey {
    actionBuilder.PatternAction("user.write", PatternType.EQUALITY).defaultHandler()(BodyParsers.parse.json) { request =>
      implicit val createReads: Reads[Future[Result]] = (
        (JsPath \ "name").read[String] and
        (JsPath \ "email").read[String] and
        (JsPath \ "password").read[String]
      )((name: String, email: String, password: String) => {
        Future { Created(
          User(name, email, password).create.toJson("id", "name", "email")
        )}
      })

      request.body.validate(createReads).get
    }
  }

  def retrieve(id: String) = ActionWithValidApiKey {
    actionBuilder.PatternAction("user", PatternType.EQUALITY).defaultHandler() {
      User.find(id.toInt) match {
        case Some(user) => Future {
          Ok(user.toJson("id", "name", "email"))
        }
        case None => Future { NotFound }
      }
    }
  }

  def delete(id: String) = ActionWithValidApiKey {
    actionBuilder.PatternAction("user.write", PatternType.EQUALITY).defaultHandler() {
      User.find(id.toInt) match {
        case Some(user) => if (user.delete) {
          Future(NoContent)
        } else { Future(BadRequest) }
        case None => Future(NotFound)
      }
    }
  }
}