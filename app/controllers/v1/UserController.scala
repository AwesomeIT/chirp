package controllers.v1

import com.google.inject._

import scala.concurrent._
import akka.actor.ActorSystem
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import org.birdfeed.chirp.database.{Query, Relation}
import org.birdfeed.chirp.database.models.User
import org.birdfeed.chirp.actions.ActionWithValidApiKey
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import scala.util._

@Singleton
class UserController @Inject()(actorSystem: ActorSystem, val dbConfigProvider: DatabaseConfigProvider)(implicit exec: ExecutionContext) extends Controller with Query {

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  def authenticate = ActionWithValidApiKey(dbConfigProvider) {
    Action.async(BodyParsers.parse.json) { request =>
      implicit val authenticateReads: Reads[Future[Result]] = (
        (JsPath \ "email").read[String] and
        (JsPath \ "password").read[String]
      )((email: String, password: String) => {
        User.authenticate(email, password).map { user =>
          val uGet = user.get
          Ok(uGet.jsonWrites.writes(uGet))
        }
      })

      request.body.validate.get
    }
  }

  def create = ActionWithValidApiKey(dbConfigProvider) {
    Action.async(BodyParsers.parse.json) { request =>
      implicit val createReads: Reads[Future[Result]] = (
        (JsPath \ "name").read[String] and
        (JsPath \ "email").read[String] and
        (JsPath \ "password").read[String]
      )((name: String, email: String, password: String) => {
        User.create(name, email, password, 2).map { created =>
          val cGet = created.get
          Created(cGet.jsonWrites.writes(cGet))
        }
      })

      request.body.validate.get
    }
  }

  def retrieve(id: String) = ActionWithValidApiKey(dbConfigProvider) {
    Action.async {
      User.find(id.toInt).map { retrieved =>
        val rGet = retrieved.get
        Ok(rGet.jsonWrites.writes(rGet))
      }
    }
  }

  def delete(id: String) = ActionWithValidApiKey(dbConfigProvider) {
    Action.async { User.delete(id.toInt).map { count => Ok(count.get.toString) } }
  }
}