
scalaVersion := "2.11.6"

scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-encoding", "UTF-8"
)

val root = Project(
  id = "rollbar-scala",
  base = file(".")
).settings(
  organization := "com.github.truerss",
  name := "rollbar-scala",
  version := "0.0.3",
  licenses += ("MIT", url("http://opensource.org/licenses/MIT")),
  crossScalaVersions := Seq("2.10.5", "2.11.6"),
  publishMavenStyle := true,
  bintrayOrganization := Some("truerss"),
  pomExtra := pomXml,
  publishArtifact in Test := false,
  libraryDependencies ++= Seq(
    "io.spray" %% "spray-json" % "1.3.2",
    "org.scalaj" %% "scalaj-http" % "2.2.1"
  )
)

val pomXml =
  <url>https://github.com/truerss/rollbar-scala</url>
    <scm>
      <url>git@github.com:truerss/rollbar-scala.git</url>
      <connection>scm:git:git@github.com:truerss/rollbar-scala.git</connection>
    </scm>
    <developers>
      <developer>
        <id>fntz</id>
        <name>mike</name>
        <url>https://github.com/fntz</url>
      </developer>
    </developers>