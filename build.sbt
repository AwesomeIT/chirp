name := """chirp"""
version := "1.0-SNAPSHOT"
lazy val root = (project in file(".")).enablePlugins(PlayScala)
scalaVersion := "2.11.7"

//fork in (Test,run) := false

resolvers ++= Seq(
  Resolver.bintrayRepo("jeremyrsmith", "maven"),
  Resolver.sonatypeRepo("snapshots")
)

libraryDependencies ++= Seq(
  evolutions,
  cache,
  ws,
  jdbc,
  //  Deprecated but there for warm fuzzies
  //  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,

  // Thank you @wsargent: https://github.com/playframework/scalatestplus-play/issues/55#issuecomment-259208681
  "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0-M1",
  "io.github.jeremyrsmith" %% "scalamock-scalatest-support" % "3.0.0",
  "com.h2database" % "h2" % "1.4.185",


// ActiveRecord
  "com.github.aselab" %% "scala-activerecord" % "0.4.0-SNAPSHOT",
  "com.github.aselab" %% "scala-activerecord-play2" % "0.4.0-SNAPSHOT",
  "com.github.aselab" %% "scala-activerecord-play2-specs" % "0.4.0-SNAPSHOT",

  // Auth
  "com.github.t3hnar" %% "scala-bcrypt" % "2.6",
  "be.objectify" %% "deadbolt-scala" % "2.5.0",

  // FRM, Evolutions support
  "org.postgresql" % "postgresql" % "9.4-1200-jdbc41",

  // Joda Time Wrapper
  "com.github.nscala-time" %% "nscala-time" % "2.14.0",

  // AWS
  "com.github.seratch" %% "awscala" % "0.5.+"
)

// Evict the 9999999999 sideloaded SLF4J jars
libraryDependencies ~= { _.map(_.exclude("org.slf4j", "slf4j-simple")) }
javaOptions in Test += "-Dconfig.file=conf/application.test.conf"

activerecordPlaySettings