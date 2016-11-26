package org.birdfeed.chirp.database.models

import be.objectify.deadbolt.scala.models.Subject
import com.github.aselab.activerecord._
import com.github.aselab.activerecord.dsl._
  import com.github.t3hnar.bcrypt._


case class User(
                 @Required var name: String,
                 @Email @Unique var email: String,
                 @Required var bcryptHash: String,
                 @Required implicit var roleId: Long = 2,
                 var foo: Option[String] = Option("bar")
               ) extends ActiveRecord with Subject {
  lazy val accessTokens = hasMany[AccessToken]
  lazy val samples = hasMany[Sample]
  lazy val scores = hasMany[Score]
  lazy val role = belongsTo[Role]

  val identifier = id.toString

  // TODO: These likely do not work
  lazy val roles = ???
  lazy val permissions = ???
}

object User extends ActiveRecordCompanion[User] {
  def apply(name: String, email: String, password: String): User = {
    User(name, email, password.bcrypt, 2)
  }

  def authenticate(email: String, password: String): Option[User] = {
    this.findBy("email", email).filter { user => password.isBcrypted(user.bcryptHash) }
  }
}
