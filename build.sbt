ThisBuild / version                               := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion                          := "2.13.16"
ThisBuild / scalafmtOnCompile                     := true
ThisBuild / libraryDependencySchemes += "dev.zio" %% "zio-json" % VersionScheme.Always
ThisBuild / evictionErrorLevel                    := Level.Info

import Dependencies.*

lazy val root = (project in file("."))
  .settings(
    name          := "BookRoom",
    scalacOptions := Settings.scalacOpts,
    Test / fork   := true,
    libraryDependencies ++= zio ++ zconfig ++ db ++ tests,
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )
