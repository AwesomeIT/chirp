package org.birdfeed.chirp.test.database

import org.birdfeed.chirp.database.Query
import org.birdfeed.chirp.support.test.BaseSpec
import org.scalatest._
import org.scalatestplus.play._
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import scala.util.Random

class QuerySpec extends BaseSpec {
  "Users" should {
    lazy val email = () => { s"steve${Random.nextInt()}@mail.net" }
    lazy val password = () => { java.util.UUID.randomUUID.toString }

    "be creatable and retrievable" in {
      for {
        created <- User.create(
          java.util.UUID.randomUUID.toString, email(), password(), 1
        )
        retrieved <- User.find(created.get.id)
      } yield created.get must equal(retrieved.get)
    }

    "be deletable" in {

      for {
        created <- User.create(
          java.util.UUID.randomUUID.toString, email(), password(), 1
        )
        deleted <- User.delete(created.get.id)
      } yield assert(deleted.get == 1)
      succeed
    }
//
//    "have working authentication" in {
//      val (current_email, current_password) = (email(), password())
//
//      val created = Await.result(
//        User.create("steve", current_email, current_password, 1), Duration.Inf
//      ).get
//
//      created must equal(
//        Await.result(
//          User.authenticate(
//            current_email, current_password
//        ), Duration.Inf).get
//      )
//    }
  }

//  "Samples" should {
//    val experiment = Await.result(
//      Experiment.create(
//        java.util.UUID.randomUUID.toString, new java.sql.Date(DateTime.now.getMillis),
//        Some(new java.sql.Date(DateTime.now.getMillis))), Duration.Inf).get
//
//    val uuid = java.util.UUID.randomUUID.toString
//    val user = Await.result(User.create(
//      java.util.UUID.randomUUID.toString, s"${uuid}@uuid.com", uuid, 1
//    ), Duration.Inf).get
//
//    val created = Await.result(Sample.create(
//      "test", user.id, "moo.wav"
//    ), Duration.Inf).get
//
//    "be creatable and retrievable" in {
//      val retrieved = Await.result(Sample.find(created.id), Duration.Inf).get
//      created must equal(retrieved)
//    }
//
//    "be associatable with experiment" in {
//      experiment << created
//      val retrievedPivot = Await.result(
//        SampleExperiment.where(_.sampleId === created.id), Duration.Inf
//      ).get
//
//      retrievedPivot.head.experimentId must equal(experiment.id)
//    }
//
//    "not be deletable if it has associations" in {
//      a [PSQLException] must be thrownBy {
//        Await.result(Sample.delete(created.id), Duration.Inf)
//      }
//    }
//  }
//
//  "Experiments" must {
//    "be creatable and retrievable" in {
//      val createdExperiment = Await.result(
//        Experiment.create(
//          java.util.UUID.randomUUID.toString, new java.sql.Date(java.util.Calendar.getInstance.getTime.getTime),
//          Some(new java.sql.Date(java.util.Calendar.getInstance.getTime.getTime))), Duration.Inf).get
//
//      createdExperiment must equal(
//        Await.result(Experiment.find(createdExperiment.id), Duration.Inf).get
//      )
//    }
//
//    "be deletable" in {
//      val createdExperiment = Await.result(
//        Experiment.create(
//          java.util.UUID.randomUUID.toString, new java.sql.Date(java.util.Calendar.getInstance.getTime.getTime),
//          Some(new java.sql.Date(java.util.Calendar.getInstance.getTime.getTime))), Duration.Inf).get
//
//      Await.result(Experiment.delete(createdExperiment.id), Duration.Inf).get must equal(
//        1 // record(s) deleted
//      )
//    }
//
//
//    "be updatable" in {
//      val createdExperiment = Await.result(
//        Experiment.create(
//          java.util.UUID.randomUUID.toString, new java.sql.Date(java.util.Calendar.getInstance.getTime.getTime),
//          Some(new java.sql.Date(java.util.Calendar.getInstance.getTime.getTime))), Duration.Inf).get
//
//      val newExperiment = Tables.ExperimentRow(createdExperiment.id, createdExperiment.name, new java.sql.Date(0), createdExperiment.endDate, createdExperiment.createdAt, createdExperiment.updatedAt)
//      Await.result(Experiment.updateById(createdExperiment.id, newExperiment), Duration.Inf)
//
//      createdExperiment must not equal (
//        Await.result(Experiment.find(createdExperiment.id), Duration.Inf).get
//      )
//    }
//  }
//
//  "Scores" must {
//    "be creatable and retrievable" in {
//      val experiment = Await.result(
//        Experiment.create(
//          java.util.UUID.randomUUID.toString, new java.sql.Date(java.util.Calendar.getInstance.getTime.getTime),
//          Some(new java.sql.Date(java.util.Calendar.getInstance.getTime.getTime))), Duration.Inf).get
//
//      val uuid = java.util.UUID.randomUUID.toString
//      val user = Await.result(User.create(
//        java.util.UUID.randomUUID.toString, s"${uuid}@uuid.com", uuid, 1
//      ), Duration.Inf).get
//
//      val sample = Await.result(Sample.create(
//        "test", user.id, "moo.wav"
//      ), Duration.Inf).get
//
//      val score = Await.result(
//        Score.create(
//          1.4, sample.id, experiment.id, user.id
//        ), Duration.Inf).get
//
//      score must equal(Await.result(Score.find(score.id), Duration.Inf).get)
//    }
//  }
}
