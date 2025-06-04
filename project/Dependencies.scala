import sbt.*

object Version {
  val zio       = "2.1.18"
  val zioJson   = "0.7.43"
  val zioConfig = "4.0.4"
  val zioHttp   = "3.3.0"

  val quill    = "4.8.5"
  val postgres = "42.7.5"
  val flyWay   = "9.22.3"

  val testContainers = "0.10.0"
}

object Dependencies {

  val zio = Seq(
    "dev.zio" %% "zio"        % Version.zio,
    "dev.zio" %% "zio-macros" % Version.zio,
    "dev.zio" %% "zio-json"   % Version.zioJson,
    "dev.zio" %% "zio-http"   % Version.zioHttp
  )

  val tests = Seq(
    "dev.zio"               %% "zio-test"                          % Version.zio % Test,
    "dev.zio"               %% "zio-test-sbt"                      % Version.zio % Test,
    "io.github.scottweaver" %% "zio-2-0-testcontainers-postgresql" % Version.testContainers,
    "io.github.scottweaver" %% "zio-2-0-db-migration-aspect"       % Version.testContainers
  )

  val zconfig = Seq(
    "dev.zio" %% "zio-config-typesafe" % Version.zioConfig,
    "dev.zio" %% "zio-config-magnolia" % Version.zioConfig
  )

  val db = Seq(
    "io.getquill"   %% "quill-jdbc-zio" % Version.quill,
    "org.postgresql" % "postgresql"     % Version.postgres,
    "org.flywaydb"   % "flyway-core"    % Version.flyWay
  )

}
