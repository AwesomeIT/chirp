package org.birdfeed.chirp.actions


import com.google.inject.Inject
import org.birdfeed.chirp.database.Query
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc._
import slick.driver.JdbcProfile

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}


case class ActionWithValidApiKey[A] @Inject()(dbConfigProvider: DatabaseConfigProvider)(action: Action[A]) extends Action[A] with Query {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  def apply(request: Request[A]): Future[Result] = {
    val key = request.headers.get("Chirp-Api-Key").getOrElse("")

    if (Await.result(ApiKey.authorize(key), Duration.Inf).getOrElse(false)) {
      action(request)
    } else { Future.successful(Results.Unauthorized) }
  }

  lazy val parser = action.parser
}