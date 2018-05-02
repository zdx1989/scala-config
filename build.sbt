organization := "com.github.zdx"

name := "scala-config"

version := "0.1.1"

scalaVersion := "2.11.8"

lazy val shapelessV = "2.3.3"
lazy val configV = "1.3.2"
lazy val scalatestV = "3.0.1"

libraryDependencies ++= Seq(
  "com.chuusai" %% "shapeless" % shapelessV,
  "com.typesafe" % "config" % configV,
  "org.scalatest" %% "scalatest" % scalatestV % Test
)

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
)


