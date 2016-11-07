package org.birdfeed.chirp.database.models

import org.birdfeed.chirp.database._
import org.birdfeed.chirp.database.models._
import slick.driver.PostgresDriver.api._

import play.api.libs.json.{Json, Writes}

import scala.concurrent.Future
import scala.util.Try

/**
  * OAuthToken relation instance representing one row object.
  *
  * @param slickTE Slick table element from codegenerated tables
  */
//class Sample (val slickTE: Tables.OauthAccessToken#TableElementType) extends Tables.OauthAccessToken(
//  slickTE.id,
//  slickTE.
//) with Relation {
//
//  // TODO: Fill this in!
//  implicit val jsonWrites: Writes[this.type] = Writes { sample =>
//    Json.obj(
//      "id" -> id,
//      "name" -> name
//    )
//  }
//}