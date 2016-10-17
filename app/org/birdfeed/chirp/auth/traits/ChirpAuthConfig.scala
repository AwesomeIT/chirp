package org.birdfeed.chirp.auth.traits

import jp.t2v.lab.play2.auth._
import org.birdfeed.chirp.auth.models._

import play.api._
import play.api.mvc._


trait ChirpAuthConfig extends AuthConfig {

  /**
    * A type that is used to identify a user.
    * `String`, `Int`, `Long` and so on.
    */
  type Id = String

  /**
    * A type that represents a user in your application.
    * `User`, `Account` and so on.
    */
  type User = Account

  /**
    * A type that is defined by every action for authorization.
    * This sample uses the following trait:
    *
    * sealed trait Role
    * case object Administrator extends Role
    * case object NormalUser extends Role
    */
  type Authority = Role


  /**
    * The session timeout in seconds
    */
  val sessionTimeoutInSeconds: Int = 3600

}
