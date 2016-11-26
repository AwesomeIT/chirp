package org.birdfeed.chirp.database

import com.github.aselab.activerecord._
import com.github.aselab.activerecord.dsl._ /* Do not remove, it scopes an implicit value */

import org.birdfeed.chirp.database.models._

object SchemaTables extends ActiveRecordTables with PlaySupport {

  val users = table[User]
  val accessTokens = table[AccessToken]
  val scores = table[Score]
  val samples = table[Sample]
  val roles = table[Role]
  val apiKeys = table[ApiKey]
  val experiments = table[Experiment]
  val permissions = table[Permission]
}