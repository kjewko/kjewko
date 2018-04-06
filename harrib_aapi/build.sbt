name := """harrib_aAPI"""
organization := "harrib_a"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.4"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test
libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.12"

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "harrib_a.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "harrib_a.binders._"
