import sbt.Keys._
import sbt._

object CommonSettings {

  lazy val compilerPlugins = Seq(
    compilerPlugin("org.typelevel" %% "kind-projector" % "0.13.2" cross CrossVersion.full),
    compilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")
  )

  implicit class CommonSettingsOps(val project: Project) extends AnyVal {
    def withCommonSettings(): Project = project.settings(
      scalacOptions ++= Seq("-Ymacro-annotations"),
      libraryDependencies ++= compilerPlugins,
      Compile / scalacOptions ++= "-Xlog-reflective-calls" ::
        "-deprecation" :: // Emit warning and location for usages of deprecated APIs.
        "-encoding" ::
        "utf-8" :: // Specify character encoding used by source files.
        "-explaintypes" :: // Explain type errors in more detail.
        "-feature" :: // Emit warning and location for usages of features that should be imported explicitly.
        "-language:existentials" :: // Existential types (besides wildcard types) can be written and inferred
        "-language:experimental.macros" :: // Allow macro definition (besides implementation and application)
        "-language:higherKinds" :: // Allow higher-kinded types
        "-language:implicitConversions" :: // Allow definition of implicit functions called views
        "-unchecked" :: // Enable additional warnings where generated code depends on assumptions.
        "-Xcheckinit" :: // Wrap field accessors to throw an exception on uninitialized access.
        "-Xlint:adapted-args" :: // Warn if an argument list is modified to match the receiver.
        "-Xlint:constant" :: // Evaluation of a constant arithmetic expression results in an error.
        "-Xlint:delayedinit-select" :: // Selecting member of DelayedInit.
        "-Xlint:inaccessible" :: // Warn about inaccessible types in method signatures.
        "-Xlint:missing-interpolator" :: // A string literal appears to be missing an interpolator id.
        "-Xlint:nullary-unit" :: // Warn when nullary methods return Unit.
        "-Xlint:option-implicit" :: // Option.apply used implicit view.
        "-Xlint:package-object-classes" :: // Class or object defined in package object.
        "-Xlint:poly-implicit-overload" :: // Parameterized overloaded implicit methods are not visible as view bounds.
        "-Xlint:private-shadow" :: // A private field (or class parameter) shadows a superclass field.
        "-Xlint:stars-align" :: // Pattern sequence wildcard must align with sequence component.
        "-Xlint:type-parameter-shadow" :: // A local type parameter shadows a type already in scope.
        "-Xlint:implicit-recursion" :: // Warn when an implicit resolves to an enclosing self-definition.
        "-Wdead-code" :: // Warn when dead code is identified.
        "-Wextra-implicit" :: // Warn when more than one implicit parameter section is defined.
        "-Wmacros:both" :: // Lints code before and after applying a macro
        "-Wnumeric-widen" :: // Warn when numerics are widened.
        "-Woctal-literal" :: // Warn on obsolete octal syntax.
        "-Wunused:locals" :: // Warn if a local definition is unused.
        "-Wunused:explicits" :: // Warn if an explicit parameter is unused.
        "-Wunused:implicits" :: // Warn if an implicit parameter is unused.
        "-Wvalue-discard" :: // Warn when non-Unit expression results are unused.
        "-Wconf:cat=deprecation:i" :: // Move warnings deprecation to info
        "-Wconf:msg=While parsing annotations in:silent" :: // Exclude warning: While parsing annotations in .../io/micrometer/micrometer-core/1.8.4/micrometer-core-1.8.4.jar(io/micrometer/core/lang/Nullable.class), could not find MAYBE in enum <none>.
        "-Ycache-plugin-class-loader:last-modified" :: // Enables caching of classloaders for compiler plugins
        "-Ycache-macro-class-loader:last-modified" :: // and macro definitions. This can lead to performance improvements.
        "-Ymacro-annotations" ::
        Nil
    )
  }

}
