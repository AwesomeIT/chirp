package org.birdfeed.chirp.database

import java.sql.Date
import java.util.Calendar

import com.google.inject.Inject
import com.github.t3hnar.bcrypt._
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.util._
import play.api.db.slick.DatabaseConfigProvider
import org.birdfeed.chirp.database.models._
import org.postgresql.util.PSQLException
import slick.backend.DatabaseConfig

import play.api.Play
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json._

import scala.util.Try

case class AuthenticationFailedException(message: String) extends Exception(message)
case class QueryFailedException(message: String) extends Exception(message)

trait Query {
  val dbConfigProvider: DatabaseConfigProvider
  val dbConfig: DatabaseConfig[JdbcProfile]

  object Sample {
    /**
      * Find Sample by ID.
      * @param id ID
      * @return Potentially a Sample relation.
      */
    def find(id: Int): Future[Try[Sample]] = {
      where(_.id === id).map(_.map(_.head))
    }
    /**
      * Filter Sample objects using Slick PG API query.
      * @param predicate Predicate for selection.
      * @return Potentially a collection of Sample relations.
      */
    def where(predicate: Tables.Sample => Rep[Boolean]): Future[Try[Seq[Sample]]] = {
      dbConfig.db.run(Tables.Sample.filter(predicate).result).map(
        (rows: Seq[Tables.Sample#TableElementType]) => {
          Try(rows.map(new Sample(_)))
        }
      )
    }
  }

  object Experiment {
    /**
      * Find experiment by id
      * @param id
      * @return Experiment if found
      */
    def find(id: Int): Future[Try[Experiment]] = {
      where(_.id === id).map(_.map(_.head))
    }

    /**
      * Create an experiment
      * @param name Name
      * @param start_date Date experiment becomes active
      * @param end_date Date experiment becomes inactive
      * @return Created experiment
      */
    def create(name: String, start_date: Date, end_date: Option[Date] = None): Future[Try[Experiment]] = {
      val currentDate = new java.sql.Date(java.util.Calendar.getInstance.getTime.getTime)
      dbConfig.db.run(
        Tables.Experiment returning Tables.Experiment.map(_.id) into (
          (experiment_row, id) => experiment_row.copy(id = id)
          ) += Tables.ExperimentRow(0, name, start_date, end_date, currentDate, currentDate)
      ).map((experiment_row: Tables.Experiment#TableElementType) => {
        Try(new Experiment(dbConfigProvider)(experiment_row))
      })
    }

    /**
      * Delete an experiment
      * @param id Id
      * @return Number of database rows deleted
      */
    def delete(id: Int): Future[Try[Int]] = {
      dbConfig.db.run(Tables.Experiment.filter(_.id === id).delete).map(Try(_))
    }

    /**
      * Update an experiment row
      * @param id Id
      * @param row Object containing experiment properties
      * @return
      */
    def updateById(id: Int, row: Tables.Experiment#TableElementType): Future[Try[Int]] = {
      dbConfig.db.run(Tables.Experiment.filter(_.id === id)
        .update(Tables.ExperimentRow(row.id, row.name, row.startDate,
          row.endDate, row.createdAt, new java.sql.Date(java.util.Calendar.getInstance.getTime.getTime)))).map(Try(_))
    }

    /**
      * Filter Experiment objects using Slick PG API query.
      * @param predicate Predicate for selection.
      * @return Potentially a collection of Experiment objects.
      */
    def where(predicate: Tables.Experiment => Rep[Boolean]): Future[Try[Seq[Experiment]]] = {
      dbConfig.db.run(Tables.Experiment.filter(predicate).result).map {
        case rows: Seq[Tables.Experiment#TableElementType] => Success(rows.map(new Experiment(dbConfigProvider)(_)))
        case error: Exception => Failure(error)
        case _ => Failure(QueryFailedException("Query was unsuccessful."))
      }
    }
  }

  object User {
    /**
      * Find User by ID.
      * @param id ID
      * @return Potentially a User relation.
      */
    def find(id: Int): Future[Try[User]] = {
      where(_.id === id).map(_.map(_.head))
    }

    /**
      * Delete User object by ID.
      * @param id ID
      * @return Potentially the ID of the deleted record.
      */
    def delete(id: Int): Future[Try[Int]] = {
      dbConfig.db.run(Tables.User.filter(_.id === id).delete).map(Try(_))
    }

    /**
      * Filter User objects using Slick PG API query.
      * @param predicate Predicate for selection.
      * @return Potentially a collection of User relations.
      */
    // Is this better?
    def where(predicate: Tables.User => Rep[Boolean]): Future[Try[Seq[User]]] = {
      dbConfig.db.run(Tables.User.filter(predicate).result).map {
        case rows: Seq[Tables.User#TableElementType] => Success(rows.map(new User(dbConfigProvider)(_)))
        case error: Exception => Failure(error)
        case _ => Failure(QueryFailedException("Query was unsuccessful."))
      }
    }
//    def where(predicate: Tables.User => Rep[Boolean]): Future[Try[Seq[User]]] = {
//      dbConfig.db.run(Tables.User.filter(predicate).result).map(
//        (rows: Seq[Tables.User#TableElementType]) => { Try(rows.map(new User(dbConfigProvider)(_))) }
//      )
//    }

    /**
      * Attempt to authenticate user.
      * @param email E-Mail address string.
      * @param password User plaintext password.
      * @return Potentially a User relation.
      */
    def authenticate(email: String, password: String): Future[Try[User]] = {
      where(_.email === email).map {
        case Success(users) => {
          users.find { user => password.isBcrypted(user.bcryptHash) } match {
            case Some(authenticated) => Success(authenticated)
            case None => Failure(AuthenticationFailedException("User not authenticated"))
          }
        }
        case Failure(ex) => Failure(ex)
      }
    }

    /**
      * Create a new User resource.
      * @param name Name
      * @param email E-Mail
      * @param password Plaintext password
      * @param roleId Privilege ID
      * @return Potentially a User relation.
      */
    def create(name: String, email: String, password: String, roleId: Int): Future[Try[User]] = {
      dbConfig.db.run(
        Tables.User returning Tables.User.map(_.id) into (
          (user_row, id) => user_row.copy(id = id)
          ) += Tables.UserRow(0, name, email, password.bcrypt, roleId)
      ).map((user_row: Tables.User#TableElementType) => {
        Try(new User(dbConfigProvider)(user_row))
      })
    }
  }
}