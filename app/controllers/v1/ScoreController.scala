package controllers.v1

import akka.actor.ActorSystem
import com.google.inject._
import org.birdfeed.chirp.actions.ActionWithValidApiKey
import org.birdfeed.chirp.database.Query
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc._
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._

import scala.concurrent._

@Singleton
class ScoreController @Inject()(actorSystem: ActorSystem, val dbConfigProvider: DatabaseConfigProvider)(implicit exec: ExecutionContext) extends Controller with Query {

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  def create = ActionWithValidApiKey(dbConfigProvider) {
    Action.async(BodyParsers.parse.json) { request =>
      implicit val createReads: Reads[Future[Result]] = (
        (JsPath \ "score").read[BigDecimal] and
        (JsPath \ "sample_id").read[Int] and
        (JsPath \ "experiment_id").read[Int] and
        (JsPath \ "user_id").read[Int]
      )((score: BigDecimal, sampleId: Int, experimentId: Int, userId: Int) => {
        Score.create(score, sampleId, experimentId, userId).map { created =>
          val createdGet = created.get
          Created(createdGet.jsonWrites.writes(createdGet))
        }
      })

      request.body.validate.get
    }
  }

  def retrieve(id: String) = ActionWithValidApiKey(dbConfigProvider) {
    Action.async { request =>
      Score.find(id.toInt).map { retrieved =>
        val rGet = retrieved.get
        Ok(rGet.jsonWrites.writes(rGet))
      }
    }
  }

  def getBySample(id: Int) = ActionWithValidApiKey(dbConfigProvider) {
    Action.async {
      Score.where(_.sampleId === id).map { s =>
        implicit val scoreFormat = Json.format[Score]
        val scores = Json.obj("scores" -> s.get)
        Ok (scores)
      }
    }
  }
}