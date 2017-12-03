name := "robinhood-scala"

version := "0.1"

crossScalaVersions := Seq("2.11.11")

organization        := "io.easley"

scalaVersion := "2.12.4"

resolvers += Resolver.bintrayRepo("hseeberger", "maven")

libraryDependencies ++= Seq(
  // For Akka 2.4.x or 2.5.x
  "com.typesafe.akka" %% "akka-http" % "10.0.10",
  // Only when running against Akka 2.5 explicitly depend on akka-streams in same version as akka-actor
  "com.typesafe.akka" %% "akka-stream" % "2.5.4", // or whatever the latest version is,
  "com.typesafe.akka" %% "akka-actor" % "2.5.4", // or whatever the latest version is
  "com.typesafe.akka" %% "akka-testkit" % "2.5.4", // or whatever the latest version is
  "com.typesafe.play" %% "play-json" % "2.6.7",
  "de.heikoseeberger" %% "akka-http-play-json" % "1.18.1",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2",
  "org.scalactic" %% "scalactic" % "3.0.4",
  "ai.x" %% "play-json-extensions" % "0.10.0",
  "org.scalatest" %% "scalatest" % "3.0.4" % "test"
)