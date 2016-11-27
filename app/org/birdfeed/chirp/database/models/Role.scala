package org.birdfeed.chirp.database.models

import com.github.aselab.activerecord._
import com.github.aselab.activerecord.dsl._

case class Role(@Required var name: String) extends ActiveRecord {

  lazy val permissions = hasAndBelongsToMany[Permission]
}

object Role extends ActiveRecordCompanion[Role]
