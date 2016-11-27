package org.birdfeed.chirp.auth.handlers

import be.objectify.deadbolt.scala.models.Subject
import be.objectify.deadbolt.scala.{AuthenticatedRequest, DeadboltHandler, DynamicResourceHandler}
import com.google.inject.Inject
import org.birdfeed.chirp.database.models.{AccessToken, User}
import play.api.mvc.{Request, Result, Results}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import com.github.aselab.activerecord.dsl._
import org.joda.time.DateTime


class ChirpDeadboltHandler extends DeadboltHandler {

  override def beforeAuthCheck[A](request: Request[A]): Future[Option[Result]] = Future {None}

  override def getDynamicResourceHandler[A](request: Request[A]): Future[Option[DynamicResourceHandler]] = Future {None}

  override def getSubject[A](request: AuthenticatedRequest[A]): Future[Option[Subject]] = {
    request.subject match {
      case Some(existingSubject) => Future(Option(existingSubject))
      case None => {
        request.headers.get("Chirp-User-Access-Token") match {
          case Some(userAccessToken) => {
//            Future { User.find(1) }
            Future {
              AccessToken.where { token =>
                (token.expiryDate.getTime.~ < DateTime.now.toDate.getTime) and
                token.token === userAccessToken
              }.headOption.map(_.user.get)
            }
          }
          case None => throw new Exception("TODO: Refactor me please, use a real exception")
        }
      }
    }
  }

  // TODO: I don't understand what I don't understand in the docs. This should do it and not
  // all of the convoluted boilerplate?
  override def onAuthFailure[A](request: AuthenticatedRequest[A]): Future[Result] = Future {
    Results.Unauthorized
  }
}