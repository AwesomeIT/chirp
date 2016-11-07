import chirp.build.SlickTableGeneratorRunner

name := """chirp"""
version := "1.0-SNAPSHOT"
lazy val root = (project in file(".")).enablePlugins(PlayScala)
scalaVersion := "2.11.7"

fork in run := true

libraryDependencies ++= Seq(
  evolutions,
  cache,
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,

  // Auth
  "com.github.t3hnar" %% "scala-bcrypt" % "2.6",
  "com.nulab-inc" %% "play2-oauth2-provider" % "1.0.0",

  // FRM, Evolutions support
  "org.postgresql" % "postgresql" % "9.4-1200-jdbc41",
  "com.typesafe.play" %% "play-slick" % "2.0.0",
  "com.typesafe.play" %% "play-slick" % "2.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "2.0.0",

  // Swagger
  "io.swagger" %% "swagger-play2" % "1.5.1"
)

// Evict the 9999999999 sideloaded SLF4J jars
libraryDependencies ~= { _.map(_.exclude("org.slf4j", "slf4j-simple")) }

lazy val generate_tables = taskKey[Unit]("generate_tables")
generate_tables := {
  SlickTableGeneratorRunner.generate
}

// Use our special test environment HOCON
javaOptions in Test += "-Dconfig.file=conf/application.test.conf"

// If seed is empty, SBT will refuse to build. Use only locally!
// compile in Compile <<= (compile in Compile).dependsOn(generate_tables)