package org.birdfeed.chirp.errors

import play.api.data.validation.ValidationError
import play.api.libs.json.{JsError, JsPath, JsValue, Json}

trait JsonError {
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
}
