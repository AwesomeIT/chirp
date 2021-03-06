package org.birdfeed.chirp.auth.handlers

import be.objectify.deadbolt.scala.cache.HandlerCache
import be.objectify.deadbolt.scala.{DeadboltHandler, HandlerKey}
import com.google.inject.{Inject, Singleton}

@Singleton
class ChirpHandlerCache @Inject()(val defaultHandler: ChirpDeadboltHandler) extends HandlerCache {
  case class ChirpKey(name: String) extends HandlerKey

  object HandlerKeys { val defaultHandler = ChirpKey("defaultHandler") }

  // HandlerKeys is an user-defined object, containing instances
  // of a case class that extends HandlerKey
  val handlers: Map[Any, DeadboltHandler] = Map(HandlerKeys.defaultHandler -> defaultHandler)

  // Get the default handler.
  override def apply(): DeadboltHandler = defaultHandler

  // Get a named handler
  override def apply(handlerKey: HandlerKey): DeadboltHandler = handlers(handlerKey)
}