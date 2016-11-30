package org.birdfeed.chirp.auth.handlers

import be.objectify.deadbolt.scala.models.Subject
import be.objectify.deadbolt.scala.{AuthenticatedRequest, DeadboltHandler, DynamicResourceHandler}
import com.github.aselab.activerecord.dsl._
import org.birdfeed.chirp.database.models.AccessToken
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
    for {
      headerToken <- request.headers.get("Chirp-Access-Token")
      databaseToken <- AccessToken.findBy("token", headerToken)
      if databaseToken.stillAlive || play.Play.isTest
    } yield databaseToken.user.get
  }

  override def onAuthFailure[A](request: AuthenticatedRequest[A]): Future[Result] = Future {
    Results.Unauthorized(
      jsonError("Your access token has either expired or is invalid. Please refresh authorization.")
    )
  }
}