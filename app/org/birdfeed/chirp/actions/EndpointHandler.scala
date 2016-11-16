package org.birdfeed.chirp.actions

import org.birdfeed.chirp.database.{AuthenticationFailedException, Relation}
import org.postgresql.util.PSQLException
import play.api.data.validation.ValidationError
import play.api.libs.json._
import play.api.mvc.Results._
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.util._

trait EndpointHandler {
  def jsonError(message: String, exception: Exception = new Exception): JsValue = {
    Json.obj(
      "error" -> message,
      "details" -> Json.obj(
        "exception_class" -> exception.getClass.toString,
        "message" -> exception.toString
      )
    )
  }

  def jsonValidationError(message: String, errors: Seq[(JsPath, Seq[ValidationError])]): JsValue = {
    Json.obj("error" -> message, "details" -> JsError.toJson(errors))
  }

  def anyWithErrorHandlingSingle[T](operation: Future[Try[T]], responseType: Results#Status): Future[Result] = {
    operation.flatMap {
      case Success(operationResponse) => Future { responseType(operationResponse.toString) }
      case Failure(ex: PSQLException) => Future { BadRequest(jsonError("A database error has occurred!", ex)) }
      case Failure(ex: Exception) => Future { InternalServerError(
        jsonError("A serious error has occurred. Please do not reattempt your request and contact support", ex
        ))}
      case Failure(_) => Future { InternalServerError("This should not have happened.") }
    }
  }

  def dtoWithErrorHandlingSingle[DTO <: Relation](operation: Future[Try[DTO]], responseType: Results#Status
  ): Future[Result] = {
    operation.flatMap {
      case Success(model) => Future { responseType(model.jsonWrites.writes(model)) }
      case Failure(ex: PSQLException) => Future { BadRequest(jsonError("A database error has occurred!", ex)) }
      case Failure(ex: AuthenticationFailedException) => Future { Unauthorized(jsonError("User not authenticated", ex)) }
      case Failure(ex: Exception) => Future { InternalServerError(
        jsonError("A serious error has occurred. Please do not reattempt your request and contact support", ex
      ))}
      case Failure(_) => Future { InternalServerError("This should not have happened.") }
    }
  }

  def dtoWithMarshallingSingle[DTO <: Relation](reads: Reads[Future[Try[DTO]]], bodyJson: JsValue, responseType: Results#Status): Future[Result] = {
    bodyJson.validate(reads).fold(
      errors => { Future { BadRequest(jsonValidationError("We failed to validate your request data!", errors)) } },
      model_operation => { dtoWithErrorHandlingSingle(model_operation, responseType) }
    )
  }
}
