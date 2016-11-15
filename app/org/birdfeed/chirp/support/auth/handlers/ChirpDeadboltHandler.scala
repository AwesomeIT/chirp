package org.birdfeed.chirp.support.auth.handlers

import be.objectify.deadbolt.scala.models.Subject
import be.objectify.deadbolt.scala.{AuthenticatedRequest, DeadboltHandler, DynamicResourceHandler}
import com.google.inject.Inject
import org.birdfeed.chirp.database.Query
import org.joda.time.DateTime
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc.{Request, Result, Results}
import slick.driver.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}


class ChirpDeadboltHandler @Inject()(val dbConfigProvider: DatabaseConfigProvider) extends DeadboltHandler with Query {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  override def beforeAuthCheck[A](request: Request[A]): Future[Option[Result]] = Future {None}

  override def getDynamicResourceHandler[A](request: Request[A]): Future[Option[DynamicResourceHandler]] = Future {None}

  override def getSubject[A](request: AuthenticatedRequest[A]): Future[Option[Subject]] = {
    request.subject match {
      case Some(existingSubject) => Future(Option(existingSubject))
      case None => {
        request.headers.get("Chirp-User-Access-Token") match {
          case Some(userAccessToken) => {
            Await.result(AccessToken.find(userAccessToken), Duration.Inf)
              .filter { token => new DateTime(token.expiresIn).isBeforeNow }
              .map { token => User.find(token.userId).map(_.toOption) }.get
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