package org.birdfeed.chirp.database.models

import com.github.aselab.activerecord._
import com.github.aselab.activerecord.dsl._

case class Score(
                 @Required var score: Double,
                 @Required var sampleId: Long,
                 @Required var experimentId: Long,
                 @Required var userId: Long
               ) extends ActiveRecord with Timestamps {
  lazy val user = belongsTo[User]
  lazy val sample = belongsTo[Sample]
  lazy val experiment = belongsTo[Experiment]
}


object Score extends ActiveRecordCompanion[Score]
