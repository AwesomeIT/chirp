package org.birdfeed.chirp.actions

import org.birdfeed.chirp.database.models.ApiKey
import org.birdfeed.chirp.errors.JsonError
import play.api.mvc._

import scala.concurrent.Future


case class ActionWithValidApiKey[A] (action: Action[A]) extends Action[A] with JsonError {
  def apply(request: Request[A]): Future[Result] = {
    request.headers.get("Chirp-Api-Key") match {
      case Some(headerToken) => {
        if (
          ApiKey.findBy("key", headerToken).isEmpty
        ) Future.successful(Results.Unauthorized(
          jsonError("You provided an invalid API key.")
        )) else action(request)
      }
      case None => Future.successful(Results.BadRequest)
    }
  }

  lazy val parser = action.parser
}