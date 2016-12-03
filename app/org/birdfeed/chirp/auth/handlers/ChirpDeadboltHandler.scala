package org.birdfeed.chirp.auth.handlers

import be.objectify.deadbolt.scala.models.Subject
import be.objectify.deadbolt.scala.{AuthenticatedRequest, DeadboltHandler, DynamicResourceHandler}
import com.github.aselab.activerecord.dsl._
import org.birdfeed.chirp.database.models.{AccessToken, Permission, Role, User}
import org.birdfeed.chirp.errors.JsonError
import play.api.mvc.{Request, Result, Results}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class ChirpDeadboltHandler extends DeadboltHandler with JsonError {


  // TODO: Do we need these?
  override def beforeAuthCheck[A](request: Request[A]): Future[Option[Result]] = Future {None}
  override def getDynamicResourceHandler[A](request: Request[A]): Future[Option[DynamicResourceHandler]] = Future {None}

  // TODO: Play.isTest is deprecated (of course it is). Use Guice everywhere
  // because everybody sucks.
  override def getSubject[A](request: AuthenticatedRequest[A]): Future[Option[Subject]] = Future {

    // Make superuser credentials
    // TODO: Refactor
    if (play.Play.isTest) {
      val uuid = java.util.UUID.randomUUID.toString

      val role = Role("test").create
      role.permissions ++= Seq(
        "user", "accessToken", "apiKey", "sample", "experiment", "score"
      ).flatMap { permission =>
        Seq(
          Permission(permission).create,
          Permission(s"$permission.write").create
        )
      }
      role.save

      val user = User(uuid, s"$uuid@uuid.com", uuid).create
      user.role := role
      user.save
      Option(user)
    } else {
      for {
        headerToken <- request.headers.get("Chirp-Access-Token")
        databaseToken <- AccessToken.findBy("token", headerToken)
        if databaseToken.stillAlive
      } yield databaseToken.user.get
    }
  }

  // TODO: Permissions error?! NOPE! DEAD ACCESS TOKEN
  // fix this and stop giving this error for everything
  override def onAuthFailure[A](request: AuthenticatedRequest[A]): Future[Result] = getSubject(request).map {
    case Some(subject) => Results.Unauthorized(jsonError("Your access token is expired, please refresh authorization."))
    case _ => Results.BadRequest(jsonError("Your access token is invalid."))
  }
}