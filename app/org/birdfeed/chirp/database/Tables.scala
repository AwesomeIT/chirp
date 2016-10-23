package org.birdfeed.chirp.database
// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object Tables extends {
  val profile = slick.driver.PostgresDriver
} with Tables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables {
  val profile: slick.driver.JdbcProfile
  import profile.api._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** DDL for all tables. Call .create to execute. */
  lazy val schema: profile.SchemaDescription = Array(Experiment.schema, PlayEvolutions.schema, PlayEvolutionsLock.schema, Role.schema, Sample.schema, SampleExperiment.schema, Score.schema, User.schema).reduceLeft(_ ++ _)
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /** Entity class storing rows of table Experiment
   *  @param id Database column id SqlType(serial), AutoInc, PrimaryKey
   *  @param name Database column name SqlType(varchar), Length(255,true)
   *  @param startDate Database column start_date SqlType(date)
   *  @param endDate Database column end_date SqlType(date), Default(None)
   *  @param createdAt Database column created_at SqlType(date)
   *  @param updatedAt Database column updated_at SqlType(date) */
  case class ExperimentRow(id: Int, name: String, startDate: java.sql.Date, endDate: Option[java.sql.Date] = None, createdAt: java.sql.Date, updatedAt: java.sql.Date)
  /** GetResult implicit for fetching ExperimentRow objects using plain SQL queries */
  implicit def GetResultExperimentRow(implicit e0: GR[Int], e1: GR[String], e2: GR[java.sql.Date], e3: GR[Option[java.sql.Date]]): GR[ExperimentRow] = GR{
    prs => import prs._
    ExperimentRow.tupled((<<[Int], <<[String], <<[java.sql.Date], <<?[java.sql.Date], <<[java.sql.Date], <<[java.sql.Date]))
  }
  /** Table description of table experiment. Objects of this class serve as prototypes for rows in queries. */
  class Experiment(_tableTag: Tag) extends Table[ExperimentRow](_tableTag, Some("chirp"), "experiment") {
    def * = (id, name, startDate, endDate, createdAt, updatedAt) <> (ExperimentRow.tupled, ExperimentRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(name), Rep.Some(startDate), endDate, Rep.Some(createdAt), Rep.Some(updatedAt)).shaped.<>({r=>import r._; _1.map(_=> ExperimentRow.tupled((_1.get, _2.get, _3.get, _4, _5.get, _6.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(serial), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column name SqlType(varchar), Length(255,true) */
    val name: Rep[String] = column[String]("name", O.Length(255,varying=true))
    /** Database column start_date SqlType(date) */
    val startDate: Rep[java.sql.Date] = column[java.sql.Date]("start_date")
    /** Database column end_date SqlType(date), Default(None) */
    val endDate: Rep[Option[java.sql.Date]] = column[Option[java.sql.Date]]("end_date", O.Default(None))
    /** Database column created_at SqlType(date) */
    val createdAt: Rep[java.sql.Date] = column[java.sql.Date]("created_at")
    /** Database column updated_at SqlType(date) */
    val updatedAt: Rep[java.sql.Date] = column[java.sql.Date]("updated_at")
  }
  /** Collection-like TableQuery object for table Experiment */
  lazy val Experiment = new TableQuery(tag => new Experiment(tag))

  /** Entity class storing rows of table PlayEvolutions
   *  @param id Database column id SqlType(int4), PrimaryKey
   *  @param hash Database column hash SqlType(varchar), Length(255,true)
   *  @param appliedAt Database column applied_at SqlType(timestamp)
   *  @param applyScript Database column apply_script SqlType(text), Default(None)
   *  @param revertScript Database column revert_script SqlType(text), Default(None)
   *  @param state Database column state SqlType(varchar), Length(255,true), Default(None)
   *  @param lastProblem Database column last_problem SqlType(text), Default(None) */
  case class PlayEvolutionsRow(id: Int, hash: String, appliedAt: java.sql.Timestamp, applyScript: Option[String] = None, revertScript: Option[String] = None, state: Option[String] = None, lastProblem: Option[String] = None)
  /** GetResult implicit for fetching PlayEvolutionsRow objects using plain SQL queries */
  implicit def GetResultPlayEvolutionsRow(implicit e0: GR[Int], e1: GR[String], e2: GR[java.sql.Timestamp], e3: GR[Option[String]]): GR[PlayEvolutionsRow] = GR{
    prs => import prs._
    PlayEvolutionsRow.tupled((<<[Int], <<[String], <<[java.sql.Timestamp], <<?[String], <<?[String], <<?[String], <<?[String]))
  }
  /** Table description of table play_evolutions. Objects of this class serve as prototypes for rows in queries. */
  class PlayEvolutions(_tableTag: Tag) extends Table[PlayEvolutionsRow](_tableTag, Some("chirp"), "play_evolutions") {
    def * = (id, hash, appliedAt, applyScript, revertScript, state, lastProblem) <> (PlayEvolutionsRow.tupled, PlayEvolutionsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(hash), Rep.Some(appliedAt), applyScript, revertScript, state, lastProblem).shaped.<>({r=>import r._; _1.map(_=> PlayEvolutionsRow.tupled((_1.get, _2.get, _3.get, _4, _5, _6, _7)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(int4), PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.PrimaryKey)
    /** Database column hash SqlType(varchar), Length(255,true) */
    val hash: Rep[String] = column[String]("hash", O.Length(255,varying=true))
    /** Database column applied_at SqlType(timestamp) */
    val appliedAt: Rep[java.sql.Timestamp] = column[java.sql.Timestamp]("applied_at")
    /** Database column apply_script SqlType(text), Default(None) */
    val applyScript: Rep[Option[String]] = column[Option[String]]("apply_script", O.Default(None))
    /** Database column revert_script SqlType(text), Default(None) */
    val revertScript: Rep[Option[String]] = column[Option[String]]("revert_script", O.Default(None))
    /** Database column state SqlType(varchar), Length(255,true), Default(None) */
    val state: Rep[Option[String]] = column[Option[String]]("state", O.Length(255,varying=true), O.Default(None))
    /** Database column last_problem SqlType(text), Default(None) */
    val lastProblem: Rep[Option[String]] = column[Option[String]]("last_problem", O.Default(None))
  }
  /** Collection-like TableQuery object for table PlayEvolutions */
  lazy val PlayEvolutions = new TableQuery(tag => new PlayEvolutions(tag))

  /** Entity class storing rows of table PlayEvolutionsLock
   *  @param lock Database column lock SqlType(int4), PrimaryKey */
  case class PlayEvolutionsLockRow(lock: Int)
  /** GetResult implicit for fetching PlayEvolutionsLockRow objects using plain SQL queries */
  implicit def GetResultPlayEvolutionsLockRow(implicit e0: GR[Int]): GR[PlayEvolutionsLockRow] = GR{
    prs => import prs._
    PlayEvolutionsLockRow(<<[Int])
  }
  /** Table description of table play_evolutions_lock. Objects of this class serve as prototypes for rows in queries. */
  class PlayEvolutionsLock(_tableTag: Tag) extends Table[PlayEvolutionsLockRow](_tableTag, Some("chirp"), "play_evolutions_lock") {
    def * = lock <> (PlayEvolutionsLockRow, PlayEvolutionsLockRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = Rep.Some(lock).shaped.<>(r => r.map(_=> PlayEvolutionsLockRow(r.get)), (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column lock SqlType(int4), PrimaryKey */
    val lock: Rep[Int] = column[Int]("lock", O.PrimaryKey)
  }
  /** Collection-like TableQuery object for table PlayEvolutionsLock */
  lazy val PlayEvolutionsLock = new TableQuery(tag => new PlayEvolutionsLock(tag))

  /** Entity class storing rows of table Role
   *  @param id Database column id SqlType(serial), AutoInc, PrimaryKey
   *  @param name Database column name SqlType(varchar), Length(255,true) */
  case class RoleRow(id: Int, name: String)
  /** GetResult implicit for fetching RoleRow objects using plain SQL queries */
  implicit def GetResultRoleRow(implicit e0: GR[Int], e1: GR[String]): GR[RoleRow] = GR{
    prs => import prs._
    RoleRow.tupled((<<[Int], <<[String]))
  }
  /** Table description of table role. Objects of this class serve as prototypes for rows in queries. */
  class Role(_tableTag: Tag) extends Table[RoleRow](_tableTag, Some("chirp"), "role") {
    def * = (id, name) <> (RoleRow.tupled, RoleRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(name)).shaped.<>({r=>import r._; _1.map(_=> RoleRow.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(serial), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column name SqlType(varchar), Length(255,true) */
    val name: Rep[String] = column[String]("name", O.Length(255,varying=true))
  }
  /** Collection-like TableQuery object for table Role */
  lazy val Role = new TableQuery(tag => new Role(tag))

  /** Entity class storing rows of table Sample
   *  @param id Database column id SqlType(serial), AutoInc, PrimaryKey
   *  @param name Database column name SqlType(varchar), Length(255,true)
   *  @param userId Database column user_id SqlType(int4)
   *  @param s3Url Database column s3_url SqlType(varchar), Length(255,true)
   *  @param createdAt Database column created_at SqlType(date)
   *  @param updatedAt Database column updated_at SqlType(date) */
  case class SampleRow(id: Int, name: String, userId: Int, s3Url: String, createdAt: java.sql.Date, updatedAt: java.sql.Date)
  /** GetResult implicit for fetching SampleRow objects using plain SQL queries */
  implicit def GetResultSampleRow(implicit e0: GR[Int], e1: GR[String], e2: GR[java.sql.Date]): GR[SampleRow] = GR{
    prs => import prs._
    SampleRow.tupled((<<[Int], <<[String], <<[Int], <<[String], <<[java.sql.Date], <<[java.sql.Date]))
  }
  /** Table description of table sample. Objects of this class serve as prototypes for rows in queries. */
  class Sample(_tableTag: Tag) extends Table[SampleRow](_tableTag, Some("chirp"), "sample") {
    def * = (id, name, userId, s3Url, createdAt, updatedAt) <> (SampleRow.tupled, SampleRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(name), Rep.Some(userId), Rep.Some(s3Url), Rep.Some(createdAt), Rep.Some(updatedAt)).shaped.<>({r=>import r._; _1.map(_=> SampleRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(serial), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column name SqlType(varchar), Length(255,true) */
    val name: Rep[String] = column[String]("name", O.Length(255,varying=true))
    /** Database column user_id SqlType(int4) */
    val userId: Rep[Int] = column[Int]("user_id")
    /** Database column s3_url SqlType(varchar), Length(255,true) */
    val s3Url: Rep[String] = column[String]("s3_url", O.Length(255,varying=true))
    /** Database column created_at SqlType(date) */
    val createdAt: Rep[java.sql.Date] = column[java.sql.Date]("created_at")
    /** Database column updated_at SqlType(date) */
    val updatedAt: Rep[java.sql.Date] = column[java.sql.Date]("updated_at")

    /** Foreign key referencing User (database name sample_user_fk) */
    lazy val userFk = foreignKey("sample_user_fk", userId, User)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Cascade)
  }
  /** Collection-like TableQuery object for table Sample */
  lazy val Sample = new TableQuery(tag => new Sample(tag))

  /** Entity class storing rows of table SampleExperiment
   *  @param id Database column id SqlType(serial), AutoInc, PrimaryKey
   *  @param sampleId Database column sample_id SqlType(int4)
   *  @param experimentId Database column experiment_id SqlType(int4) */
  case class SampleExperimentRow(id: Int, sampleId: Int, experimentId: Int)
  /** GetResult implicit for fetching SampleExperimentRow objects using plain SQL queries */
  implicit def GetResultSampleExperimentRow(implicit e0: GR[Int]): GR[SampleExperimentRow] = GR{
    prs => import prs._
    SampleExperimentRow.tupled((<<[Int], <<[Int], <<[Int]))
  }
  /** Table description of table sample_experiment. Objects of this class serve as prototypes for rows in queries. */
  class SampleExperiment(_tableTag: Tag) extends Table[SampleExperimentRow](_tableTag, Some("chirp"), "sample_experiment") {
    def * = (id, sampleId, experimentId) <> (SampleExperimentRow.tupled, SampleExperimentRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(sampleId), Rep.Some(experimentId)).shaped.<>({r=>import r._; _1.map(_=> SampleExperimentRow.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(serial), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column sample_id SqlType(int4) */
    val sampleId: Rep[Int] = column[Int]("sample_id")
    /** Database column experiment_id SqlType(int4) */
    val experimentId: Rep[Int] = column[Int]("experiment_id")

    /** Foreign key referencing Experiment (database name sample_experiment_experiment_id_fkey) */
    lazy val experimentFk = foreignKey("sample_experiment_experiment_id_fkey", experimentId, Experiment)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Sample (database name sample_experiment_sample_id_fkey) */
    lazy val sampleFk = foreignKey("sample_experiment_sample_id_fkey", sampleId, Sample)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table SampleExperiment */
  lazy val SampleExperiment = new TableQuery(tag => new SampleExperiment(tag))

  /** Entity class storing rows of table Score
   *  @param id Database column id SqlType(serial), AutoInc, PrimaryKey
   *  @param score Database column score SqlType(numeric)
   *  @param sampleExperimentId Database column sample_experiment_id SqlType(int4)
   *  @param userId Database column user_id SqlType(int4) */
  case class ScoreRow(id: Int, score: scala.math.BigDecimal, sampleExperimentId: Int, userId: Int)
  /** GetResult implicit for fetching ScoreRow objects using plain SQL queries */
  implicit def GetResultScoreRow(implicit e0: GR[Int], e1: GR[scala.math.BigDecimal]): GR[ScoreRow] = GR{
    prs => import prs._
    ScoreRow.tupled((<<[Int], <<[scala.math.BigDecimal], <<[Int], <<[Int]))
  }
  /** Table description of table score. Objects of this class serve as prototypes for rows in queries. */
  class Score(_tableTag: Tag) extends Table[ScoreRow](_tableTag, Some("chirp"), "score") {
    def * = (id, score, sampleExperimentId, userId) <> (ScoreRow.tupled, ScoreRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(score), Rep.Some(sampleExperimentId), Rep.Some(userId)).shaped.<>({r=>import r._; _1.map(_=> ScoreRow.tupled((_1.get, _2.get, _3.get, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(serial), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column score SqlType(numeric) */
    val score: Rep[scala.math.BigDecimal] = column[scala.math.BigDecimal]("score")
    /** Database column sample_experiment_id SqlType(int4) */
    val sampleExperimentId: Rep[Int] = column[Int]("sample_experiment_id")
    /** Database column user_id SqlType(int4) */
    val userId: Rep[Int] = column[Int]("user_id")

    /** Foreign key referencing SampleExperiment (database name score_sample_experiment_id_fkey) */
    lazy val sampleExperimentFk = foreignKey("score_sample_experiment_id_fkey", sampleExperimentId, SampleExperiment)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing User (database name score_user_fk) */
    lazy val userFk = foreignKey("score_user_fk", userId, User)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Cascade)

    /** Index over (id,sampleExperimentId) (database name index_score_sample_experiment) */
    val index1 = index("index_score_sample_experiment", (id, sampleExperimentId))
  }
  /** Collection-like TableQuery object for table Score */
  lazy val Score = new TableQuery(tag => new Score(tag))

  /** Entity class storing rows of table User
   *  @param id Database column id SqlType(serial), AutoInc, PrimaryKey
   *  @param name Database column name SqlType(varchar), Length(255,true)
   *  @param email Database column email SqlType(varchar), Length(255,true)
   *  @param bcryptHash Database column bcrypt_hash SqlType(varchar), Length(255,true)
   *  @param roleId Database column role_id SqlType(int4) */
  case class UserRow(id: Int, name: String, email: String, bcryptHash: String, roleId: Int)
  /** GetResult implicit for fetching UserRow objects using plain SQL queries */
  implicit def GetResultUserRow(implicit e0: GR[Int], e1: GR[String]): GR[UserRow] = GR{
    prs => import prs._
    UserRow.tupled((<<[Int], <<[String], <<[String], <<[String], <<[Int]))
  }
  /** Table description of table user. Objects of this class serve as prototypes for rows in queries. */
  class User(_tableTag: Tag) extends Table[UserRow](_tableTag, Some("chirp"), "user") {
    def * = (id, name, email, bcryptHash, roleId) <> (UserRow.tupled, UserRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(name), Rep.Some(email), Rep.Some(bcryptHash), Rep.Some(roleId)).shaped.<>({r=>import r._; _1.map(_=> UserRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(serial), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column name SqlType(varchar), Length(255,true) */
    val name: Rep[String] = column[String]("name", O.Length(255,varying=true))
    /** Database column email SqlType(varchar), Length(255,true) */
    val email: Rep[String] = column[String]("email", O.Length(255,varying=true))
    /** Database column bcrypt_hash SqlType(varchar), Length(255,true) */
    val bcryptHash: Rep[String] = column[String]("bcrypt_hash", O.Length(255,varying=true))
    /** Database column role_id SqlType(int4) */
    val roleId: Rep[Int] = column[Int]("role_id")

    /** Foreign key referencing Role (database name user_role_fk) */
    lazy val roleFk = foreignKey("user_role_fk", roleId, Role)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.Restrict)

    /** Index over (id,email) (database name index_user_id_email) */
    val index1 = index("index_user_id_email", (id, email))
  }
  /** Collection-like TableQuery object for table User */
  lazy val User = new TableQuery(tag => new User(tag))
}
