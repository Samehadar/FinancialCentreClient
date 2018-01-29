name := "FinancialCentreClient"

version := "1.0"

scalaVersion := "2.12.1"

val circeVersion = "0.9.0"

libraryDependencies ++= Seq(
  // JSON
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,
  "io.circe" %% "circe-core" % circeVersion,
  "com.typesafe.akka" %% "akka-http" % "10.0.7",
  "org.typelevel" %% "cats-core" % "1.0.1"
)