import CommonSettings._
import sbt.Keys._
import sbt._

object MakeProject {
  def makeProject(projectName: String, projectVersion: String, rootDirPath: Option[String] = None)(
      deps: Seq[ModuleID]
  ): Project = {
    Project(projectName, file(rootDirPath.getOrElse(projectName)))
      .settings(
        name := projectName,
        libraryDependencies ++= deps ++ Seq("org.scala-lang" % "scala-reflect" % scalaVersion.value),
        version := projectVersion,
      )
  }

  def makeRootProject(projectName: String)(of: ProjectReference*): Project =
    Project(projectName, file(projectName))
      .aggregate(of: _*)
}