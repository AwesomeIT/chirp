package org.birdfeed.chirp.database.models

import java.util.Date

import com.github.aselab.activerecord._
import com.github.aselab.activerecord.dsl._

case class AccessToken(
                  @Required var userId: Long,
                  @Required @Unique var token: String,
                  @Required var refreshToken: String,
                  @Required var expiryDate: Date
                 ) extends ActiveRecord with Timestamps {
  lazy val user = belongsTo[User]
}

object AccessToken extends ActiveRecordCompanion[AccessToken]
