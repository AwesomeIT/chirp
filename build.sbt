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
  "jp.t2v" %% "play2-auth"        % "0.14.2",
  "jp.t2v" %% "play2-auth-test"   % "0.14.2" % Test,

  // FRM, Evolutions support
  "org.postgresql" % "postgresql" % "9.4-1200-jdbc41",
  "com.typesafe.play" %% "play-slick" % "2.0.0",
  "com.typesafe.play" %% "play-slick" % "2.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "2.0.0"
)

lazy val generate_tables = taskKey[Unit]("generate_tables")
generate_tables := {
  SlickTableGeneratorRunner.generate
}

// If seed is empty, SBT will refuse to build. Use only locally!
// compile in Compile <<= (compile in Compile).dependsOn(generate_tables)
