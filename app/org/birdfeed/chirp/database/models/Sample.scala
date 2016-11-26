package org.birdfeed.chirp.database.models

import com.github.aselab.activerecord._
import com.github.aselab.activerecord.dsl._

case class Sample(
                 @Required var name: String,
                 @Required var userId: Long,
                 @Required var s3Url: String
                 ) extends ActiveRecord with Timestamps {
  lazy val user = belongsTo[User]
  lazy val experiments = hasAndBelongsToMany[Experiment]
}

object Sample extends ActiveRecordCompanion[Sample]
