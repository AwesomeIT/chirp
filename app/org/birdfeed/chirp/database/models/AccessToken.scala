package org.birdfeed.chirp.database.models

import com.google.inject.Inject
import org.birdfeed.chirp.database.{Query, Relation, Tables}
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.{Json, Writes}
import slick.driver.JdbcProfile

/**
  * AccessToken relation instance representing one row object.
  *
  * @param slickTE Slick table element from codegenerated tables
  */
class AccessToken @Inject()(val dbConfigProvider: DatabaseConfigProvider)(val slickTE: Tables.AccessToken#TableElementType) extends Tables.AccessTokenRow(
  slickTE.userId, slickTE.token, slickTE.refreshToken, slickTE.issueTime, slickTE.expiresIn) with Relation[Tables#AccessTokenRow] with Query {

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  implicit val jsonWrites: Writes[this.type] = Writes { access_token =>
    Json.obj(
      "access_token" -> token,
      "refresh_token" -> refreshToken,
      "issue_time" -> issueTime,
      "expires_in" -> expiresIn
    )
  }
}