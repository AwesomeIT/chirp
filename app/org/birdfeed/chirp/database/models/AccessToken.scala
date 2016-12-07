package org.birdfeed.chirp.database.models

import java.sql.Timestamp
import java.util.Date

import com.github.aselab.activerecord._
import com.github.aselab.activerecord.dsl._
import org.joda.time.DateTime

case class AccessToken(
                  @Required var userId: Long,
                  @Required @Unique var token: String,
                  @Required @Unique var refreshToken: String,
                  @Required var expiryDate: Timestamp
                 ) extends ActiveRecord with Timestamps {
  lazy val user = belongsTo[User]

  def stillAlive: Boolean = { new DateTime(expiryDate).isAfter(DateTime.now) }
}

object AccessToken extends ActiveRecordCompanion[AccessToken] {
  def mint(userId: Long): AccessToken = {
    AccessToken(
      userId,
      java.util.UUID.randomUUID.toString,
      java.util.UUID.randomUUID.toString,
      new Timestamp(DateTime.now.plusDays(1).getMillis)
    )
  }
}
