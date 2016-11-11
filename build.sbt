import chirp.build.SlickTableGeneratorRunner

name := """chirp"""
version := "1.0-SNAPSHOT"
lazy val root = (project in file(".")).enablePlugins(PlayScala)
scalaVersion := "2.11.7"

fork in run := true

resolvers += Resolver.bintrayRepo("jeremyrsmith", "maven")

libraryDependencies ++= Seq(
  evolutions,
  cache,
  ws,
  //  Deprecated but there for warm fuzzies
  //  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,

  // Thank you @wsargent: https://github.com/playframework/scalatestplus-play/issues/55#issuecomment-259208681
  "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0-M1" % Test,
  "io.github.jeremyrsmith" %% "scalamock-scalatest-support" % "3.0.0" % Test,

  // Auth
  "com.github.t3hnar" %% "scala-bcrypt" % "2.6",
  "com.nulab-inc" %% "play2-oauth2-provider" % "1.0.0",

  // FRM, Evolutions support
  "org.postgresql" % "postgresql" % "9.4-1200-jdbc41",
  "com.typesafe.play" %% "play-slick" % "2.0.0",
  "com.typesafe.play" %% "play-slick" % "2.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "2.0.0",

  // Joda Time Wrapper
  "com.github.nscala-time" %% "nscala-time" % "2.14.0",

  // AWS
  "com.github.seratch" %% "awscala" % "0.5.+"
)

// Evict the 9999999999 sideloaded SLF4J jars
libraryDependencies ~= { _.map(_.exclude("org.slf4j", "slf4j-simple")) }

lazy val generate_tables = taskKey[Unit]("generate_tables")
generate_tables := {
  SlickTableGeneratorRunner.generate
}

javaOptions in Test += "-Dconfig.file=conf/application.test.conf"

// If seed is empty, SBT will refuse to build. Use only locally!
// compile in Compile <<= (compile in Compile).dependsOn(generate_tables)