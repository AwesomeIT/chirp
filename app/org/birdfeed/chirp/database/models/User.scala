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
               ) extends ActiveRecord with Subject with Timestamps {
  lazy val accessTokens = hasMany[AccessToken]
  lazy val samples = hasMany[Sample]
  lazy val scores = hasMany[Score]

  lazy val modelRoles = hasAndBelongsToMany[Role]
  lazy val modelPermissions = hasManyThrough[Permission, Role](modelRoles)

  val identifier = id.toString

  def roles = modelRoles.toList
  def permissions = modelPermissions.toList
}

object User extends ActiveRecordCompanion[User] {
  def apply(name: String, email: String, password: String): User = {
    User(name, email, password.bcrypt, 2)
  }

  def authenticate(email: String, password: String): Option[AccessToken] = {
    this.findBy("email", email)
      .collect {
        case user if password.isBcrypted(user.bcryptHash) => AccessToken.mint(user.id).create
      }
  }

  def refresh(refreshToken: String): Option[AccessToken] = {
    AccessToken.findBy("refreshToken", refreshToken).collect {
      case token if User.find(token.userId).isEmpty => AccessToken.mint(token.userId).create
    }
  }
}
