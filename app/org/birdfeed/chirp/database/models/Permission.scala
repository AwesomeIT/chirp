package org.birdfeed.chirp.database.models

import com.github.aselab.activerecord._
import com.github.aselab.activerecord.dsl._

case class Permission(@Required var value: String) extends ActiveRecord with be.objectify.deadbolt.scala.models.Permission {

  lazy val roles = hasAndBelongsToMany[Role]
}

object Permission extends ActiveRecordCompanion[Permission]
