package org.birdfeed.chirp.errors

import play.api.http.HttpErrorHandler
import play.api.mvc._
import play.api.mvc.Results._

import scala.concurrent._
import com.google.inject.Singleton
import org.birdfeed.chirp.database.AuthenticationFailedException
import org.postgresql.util.PSQLException

@Singleton
class Handler extends HttpErrorHandler with JsonError {

  def onClientError(request: RequestHeader, statusCode: Int, message: String) = {
    Future.successful(
      Status(statusCode)("A client error occurred: " + message)
    )
  }

  def onServerError(request: RequestHeader, exception: Throwable) = {
    Future.successful(exception match {
      case ex: PSQLException => {
        BadRequest(jsonError("A database error has occurred.", ex))
      }
      case ex: AuthenticationFailedException => {
        Unauthorized(jsonError("Invalid credentials.", ex))
      }
      case ex: Exception => {
        InternalServerError(jsonError("Critical error."))
    }})
  }
}
