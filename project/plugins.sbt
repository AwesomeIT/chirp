// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.5.9")

// Web plugins
addSbtPlugin("com.typesafe.sbt" % "sbt-coffeescript" % "1.0.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-less" % "1.1.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-jshint" % "1.0.4")

addSbtPlugin("com.typesafe.sbt" % "sbt-rjs" % "1.0.8")

addSbtPlugin("com.typesafe.sbt" % "sbt-digest" % "1.1.1")

addSbtPlugin("com.typesafe.sbt" % "sbt-mocha" % "1.1.0")

addSbtPlugin("org.irundaia.sbt" % "sbt-sassify" % "1.4.6")

// Additional resolves
resolvers ++= Seq(
  Resolver.url(
    "bintray-sbt-plugin-releases",
    url("http://dl.bintray.com/content/sbt/sbt-plugin-releases"))(
    Resolver.ivyStylePatterns),
  Classpaths.sbtPluginReleases,
  "Typesafe Repository" at "https://repo.typesafe.com/typesafe/releases/"
)

// dotenv
addSbtPlugin("me.lessis" % "bintray-sbt" % "0.1.1")
addSbtPlugin("au.com.onegeek" %% "sbt-dotenv" % "1.1.36")

// IDEA
addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "latest.integration")

// Codacy test runner
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.3.5")
addSbtPlugin("com.codacy" % "sbt-codacy-coverage" % "1.3.4")