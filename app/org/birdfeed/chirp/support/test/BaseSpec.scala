package org.birdfeed.chirp.support.test

import org.birdfeed.chirp.database.Query
import org.scalatest._
import org.scalatestplus.play.OneServerPerSuite
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

/**
  * We run into the self-type issue, so we extend one common interface
  *
  * TODO: OSPT is deprecated, use alternative
  */

trait BaseSpec extends AsyncWordSpec with TestSuite with OneServerPerSuite with MustMatchers with Query with ParallelTestExecution {
  val dbConfigProvider = app.injector.instanceOf(classOf[DatabaseConfigProvider])
  val dbConfig = dbConfigProvider.get[JdbcProfile]
}