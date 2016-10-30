package org.birdfeed.chirp.test.database

import org.scalatest._
import org.scalatestplus.play._

import scala.util._
import scala.concurrent._
import scala.concurrent.duration.Duration

import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import org.birdfeed.chirp.database.Query
import scala.concurrent.duration._
import scala.concurrent._
import scala.util.Random
import org.birdfeed.chirp.database.{Query, Tables}
import org.birdfeed.chirp.database.Query.Experiment.Experiment

class QuerySpec extends WordSpec with MustMatchers with OneServerPerSuite with Query {
  // This is what happens when your framework is written caring more about
  // what the Java crowd wants. Bullshit. Scala DI is its trait system.
  val dbConfigProvider = app.injector.instanceOf(classOf[DatabaseConfigProvider])
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  "Users" should {
    lazy val email = () => { s"steve${Random.nextInt()}@mail.net" }
    lazy val password = () => { java.util.UUID.randomUUID.toString }

    "be creatable and retrievable" in {
      val created = Await.result(User.create(
        java.util.UUID.randomUUID.toString, email(), password(), 1
      ), Duration.Inf).get

      created must equal(
        Await.result(User.find(created.id), Duration.Inf).get
      )
    }

    "be deletable" in {
      val created = Await.result(User.create(
        java.util.UUID.randomUUID.toString, email(), password(), 1
      ), Duration.Inf).get

      Await.result(User.delete(created.id), Duration.Inf).get must equal(
        1 // record(s) deleted
      )
    }

    "have working authentication" in {
      val (current_email, current_password) = (email(), password())

      val created = Await.result(
        User.create("steve", current_email, current_password, 1), Duration.Inf
      ).get

      created must equal(
        Await.result(
          User.authenticate(
            current_email, current_password
        ), Duration.Inf).get
      )
    }
  }

  "Experiments" must {
    "be creatable and retrievable" in {
      val createdExperiment = Await.result(
        Query.Experiment.create(
          java.util.UUID.randomUUID.toString, new java.sql.Date(java.util.Calendar.getInstance.getTime.getTime),
          Some(new java.sql.Date(java.util.Calendar.getInstance.getTime.getTime))), Duration.Inf
      ) match {
        case Some(experiment: Query.Experiment.Experiment) => experiment
        case None => fail("Experiment not created")
      }

      val retrievedExperiment = Await.result(
        Query.Experiment.find(createdExperiment.slickTE.id), Duration.Inf
      ) match {
        case Some(experiment: Query.Experiment.Experiment) => experiment
        case None => fail("Could not retrieve user")
      }

      createdExperiment must equal (retrievedExperiment)
    }
    "be deletable" in {
      val createdExperiment = Await.result(
        Query.Experiment.create(
          java.util.UUID.randomUUID.toString, new java.sql.Date(java.util.Calendar.getInstance.getTime.getTime),
          Some(new java.sql.Date(java.util.Calendar.getInstance.getTime.getTime))), Duration.Inf
      ) match {
        case Some(experiment: Query.Experiment.Experiment) => experiment
        case None => fail("Experiment not created")
      }

      val objId = createdExperiment.slickTE.id

      Await.result(
        Query.Experiment.delete(objId), Duration.Inf
      )

      Await.result(
        Query.Experiment.find(objId), Duration.Inf
      ) must equal (None)
    }
    "be updatable" in {
      val createdExperiment = Await.result(
        Query.Experiment.create(
          java.util.UUID.randomUUID.toString, new java.sql.Date(java.util.Calendar.getInstance.getTime.getTime),
          Some(new java.sql.Date(java.util.Calendar.getInstance.getTime.getTime))), Duration.Inf
      ) match {
        case Some(experiment: Query.Experiment.Experiment) => experiment
        case None => fail("Experiment not created")
      }
      val row = createdExperiment.slickTE
      val newExperiment = Tables.ExperimentRow(row.id, row.name, new java.sql.Date(0), row.endDate, row.createdAt, row.updatedAt)
      Await.result(Query.Experiment.updateById(row.id, newExperiment), Duration.Inf)

      Await.result(
        Query.Experiment.find(createdExperiment.slickTE.id), Duration.Inf
      ) must not equal (createdExperiment)
    }
  }
}
