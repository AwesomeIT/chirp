package org.birdfeed.chirp.auth.hooks

import be.objectify.deadbolt.scala.cache.HandlerCache
import org.birdfeed.chirp.auth.handlers.ChirpHandlerCache
import play.api.inject.{Binding, Module}
import play.api.{Configuration, Environment}

class ChirpDeadboltHook extends Module {
  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] = Seq(
    bind[HandlerCache].to[ChirpHandlerCache]
  )
}