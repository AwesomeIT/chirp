package org.birdfeed.chirp.errors

import com.google.inject.Singleton
import org.postgresql.util.PSQLException
import play.api.http.HttpErrorHandler
import play.api.mvc.Results._
import play.api.mvc._

import scala.concurrent._

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
        // TODO: Fix this
//      case ex: AuthenticationFailedException => {
//        Unauthorized(jsonError("Invalid credentials.", ex))
//      }
      case ex: Exception => {
        InternalServerError(jsonError("A ISE has occurred.", ex))
    }})
  }
}
