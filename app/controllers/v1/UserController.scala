package controllers.v1

import com.google.inject._

import scala.concurrent._
import akka.actor.ActorSystem
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import org.birdfeed.chirp.database.{Query, Relation}
import org.birdfeed.chirp.database.models.User
import org.birdfeed.chirp.support.actions.{ActionWithValidApiKey, EndpointHandler}
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import scala.util._

@Singleton
class UserController @Inject()(actorSystem: ActorSystem, val dbConfigProvider: DatabaseConfigProvider)(implicit exec: ExecutionContext) extends Controller with EndpointHandler with Query {

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  def authenticate = Action.async(BodyParsers.parse.json) { request =>
    val authenticateReads: Reads[Future[Try[User]]] = (
      (JsPath \ "email").read[String] and
      (JsPath \ "password").read[String]
    )((email: String, password: String) => {
      User.authenticate(email, password)
    })

    dtoWithMarshallingSingle(authenticateReads, request.body, Ok)
  }

  def create = ActionWithValidApiKey(dbConfigProvider) {
    Action.async(BodyParsers.parse.json) { request =>
      val createReads: Reads[Future[Try[User]]] = (
          (JsPath \ "name").read[String] and
          (JsPath \ "email").read[String] and
          (JsPath \ "password").read[String]
      )((name: String, email: String, password: String) => {
        User.create(name, email, password, 2)
      })

      dtoWithMarshallingSingle(createReads, request.body, Created)
    }
  }

//  def create = Action.async(BodyParsers.parse.json) { request =>
//  }

  def retrieve(id: String) = Action.async { request =>
    dtoWithErrorHandlingSingle(User.find(id.toInt), Ok)
  }

  def delete(id: String) = Action.async { request =>
    anyWithErrorHandlingSingle(User.delete(id.toInt), Ok)
  }
}