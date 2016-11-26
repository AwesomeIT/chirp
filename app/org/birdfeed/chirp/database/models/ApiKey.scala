package org.birdfeed.chirp.database.models

import com.github.aselab.activerecord._
import com.github.aselab.activerecord.dsl._

case class ApiKey(
                   @Required var key: String,
                   @Required var active: Boolean
                 ) extends ActiveRecord with Timestamps

object ApiKey extends ActiveRecordCompanion[ApiKey] {
  def apply(active: Boolean): ApiKey = {
    ApiKey(java.util.UUID.randomUUID.toString, active)
  }
}
