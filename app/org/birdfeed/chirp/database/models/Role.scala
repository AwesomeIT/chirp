package org.birdfeed.chirp.database.models

import com.github.aselab.activerecord._
import com.github.aselab.activerecord.dsl._
import com.google.inject.Inject

case class Role @Inject()(
                 @Required var name: String
               ) extends ActiveRecord with be.objectify.deadbolt.scala.models.Role {
  lazy val users = hasMany[User]
  lazy val permissions = hasAndBelongsToMany[Permission]
}

object Role extends ActiveRecordCompanion[Role]
