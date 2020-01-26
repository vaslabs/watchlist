name := "watchlist"

version := "0.1"

scalaVersion := "2.13.1"


lazy val watchlist = (
  project in file ("."))
  .aggregate(model, protocol, endpoints, service)

lazy val service =
  (project in file("watchlist-service"))
  .settings(libraryDependencies ++= Dependencies.Module.service)
  .dependsOn(endpoints, protocol)

lazy val endpoints =
  (project in file("endpoints"))
  .settings(libraryDependencies ++= Dependencies.Module.endpoints)
  .dependsOn(model)


lazy val protocol =
  (project in file ("protocol"))
  .settings(libraryDependencies ++= Dependencies.Module.protocol)


lazy val model =
  project in file("model")





