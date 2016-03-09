import sbt._
import sbt.Keys._

object RollbarBuild extends Build {
  val opts = Seq(
    scalacOptions ++= Seq(
      "-deprecation",
      "-feature",
      "-encoding", "UTF-8"
    ),
    scalaVersion := "2.11.6"
  )

  lazy val main = Project(
    id = "rollbar-scala",
    base = file("."),
    settings = opts ++ Seq(
      organization := "com.github.truerss",
      name := "rollbar-scala",
      version := "0.0.1",
      licenses += ("MIT", url("http://opensource.org/licenses/MIT")),
      libraryDependencies ++= Seq(
        "io.spray" %% "spray-json" % "1.3.2",
        "org.scalaj" %% "scalaj-http" % "2.2.1"
      )
    )
  )

}

