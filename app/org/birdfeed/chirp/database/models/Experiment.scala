package org.birdfeed.chirp.database.models

import java.util.Date

import com.github.aselab.activerecord._
import com.github.aselab.activerecord.dsl._

case class Experiment(
                      @Required var name: String,
                      @Required var userId: Long
                     ) extends ActiveRecord with Timestamps {

  lazy val samples = hasAndBelongsToMany[Sample]
}

object Experiment extends ActiveRecordCompanion[Experiment]
