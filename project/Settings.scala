object Settings {

  val scalacOpts = Seq(
    "-Ymacro-annotations",
    "-deprecation",
    "-encoding",
    "utf-8",
    "-explaintypes",
    "-feature",
    "-unchecked",
    "-language:postfixOps",
    "-language:higherKinds",
    "-language:implicitConversions",
    "-Xcheckinit",
    "-Xfatal-warnings"
  )

}
