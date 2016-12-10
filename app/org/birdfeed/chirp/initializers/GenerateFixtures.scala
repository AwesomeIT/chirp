package org.birdfeed.chirp.initializers

import com.google.inject.{Inject, Singleton}
import org.birdfeed.chirp.database.SchemaTables
import org.birdfeed.chirp.database.models._
import play.api.{Environment, Logger}
import play.api.inject.DefaultApplicationLifecycle

import scala.sys.process.Process
import scala.util.Random
import com.github.t3hnar.bcrypt._



@Singleton
class GenerateFixtures @Inject()(lifecycle: DefaultApplicationLifecycle, env: Environment) {
  SchemaTables.initialize

  // Create Users
  val admin = User("Test Admin", "test@admin.com", "foobar123".bcrypt, 1).create
  val researcher = User("Test Researcher", "test@researcher.com", "barbaz123".bcrypt, 2).create
  val participant = User("Test Participant", "test@participant.com", "quux456".bcrypt, 3).create

  // Create some samples
  val samples = 1 to 50 map { n =>
    Sample(s"Sound Bite $n", researcher.id, "https://not.a.real.s3.url").create
  }

  // Create some experiments
  1 to 10 foreach { n =>
    val e = Experiment(s"Experiment $n", researcher.id).create
    e.samples := Random.shuffle(samples).take(Random.nextInt(50))
  }

  // Generate an API key
  Logger.debug(s"Fixture generation complete! Use this API key: ${ApiKey(true).create.key}")

  SchemaTables.cleanup

  // TODO: This is not graceful at all
  lifecycle.stop
  Process("kill `cat target/universal/stage/RUNNING_PID`")
}