import MakeProject._
import CommonSettings._

ThisBuild / scalaVersion := "2.13.10"

ThisBuild / publish / skip := true
ThisBuild / publishLocal / skip := true

lazy val version = "0.0.1"

lazy val root = (project in file("."))
  .settings(
    Compile / doc / sources := Seq.empty,
    Compile / doc / scalacOptions += "-no-link-warnings",
    name := "the-seer"
  )
  .aggregate(
    examples,
    `the-seer-compiler-plugin`,
  )

lazy val examples =
  makeProject("examples", version)(
    Seq(
      // compilerPlugin("prototype" %% "the-seer-compiler-plugin" % "develop-SNAPSHOT"),
      "org.scalatest" %% "scalatest" % "3.2.16" % Test,
      // Можно закомментировать, чтобы убедиться, что % Optional действует
      "io.circe"        %% "circe-generic" % "0.14.1",
      "io.circe"        %% "circe-core" % "0.14.1",
      "com.tethys-json" %% "tethys-core" % "0.26.0",
      "com.tethys-json" %% "tethys-jackson" % "0.26.0",
      "com.tethys-json" %% "tethys-derivation" % "0.26.0",
    )
  )
    .dependsOn(
      `the-seer-compiler-plugin`,
    )
    .settings(
      scalacOptions ++= Seq(
        "-Xplugin:" + packageBin.in(Compile).in(`the-seer-compiler-plugin`).value,
        "-P:the-seer:enabled"
      ),
    )
    .withCommonSettings()

lazy val `the-seer-compiler-plugin` =
  makeProject("the-seer-compiler-plugin", version, Some("the-seer-compiler-plugin"))(Seq.empty)
    .settings(
      libraryDependencies += scalaOrganization.value % "scala-compiler" % scalaVersion.value
    )
    .withCommonSettings()