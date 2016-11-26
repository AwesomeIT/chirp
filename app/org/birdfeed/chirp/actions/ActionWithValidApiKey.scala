package org.birdfeed.chirp.actions

import org.birdfeed.chirp.database.models.ApiKey
import play.api.mvc._

import scala.concurrent.Future


case class ActionWithValidApiKey[A] (action: Action[A]) extends Action[A] {
  def apply(request: Request[A]): Future[Result] = {
    ApiKey.findBy(
      "key", request.headers.get("Chirp-Api-Key").get
    ) match {
      case Some(valid_token) => action(request)
      case None => Future.successful(Results.Unauthorized)
    }
  }

  lazy val parser = action.parser
}