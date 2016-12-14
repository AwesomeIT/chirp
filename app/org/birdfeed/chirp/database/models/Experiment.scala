package org.birdfeed.chirp.database.models

import java.util.Date

import com.github.aselab.activerecord._
import com.github.aselab.activerecord.dsl._
import com.google.inject.Inject

case class Experiment @Inject()(
                      @Required var name: String,
                      @Required var userId: Long
                     ) extends ActiveRecord with Timestamps {

  lazy val samples = hasAndBelongsToMany[Sample]
  lazy val scores = hasMany[Score]
}

object Experiment extends ActiveRecordCompanion[Experiment]
