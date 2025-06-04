package com.k.bookroom.conf

import zio.config.magnolia.deriveConfig
import zio.{Config, Layer, ZLayer}

final case class RootConfig(
  config: AppConfig
)
final case class AppConfig(
  db: DbConfig
)

final case class DbConfig(
  url: String,
  user: String,
  password: String
)

object Configuration {
  import zio.config.typesafe._

  val layer: Layer[Config.Error, DbConfig] =
    for {
      appConfig <- ZLayer.fromZIO(TypesafeConfigProvider.fromResourcePath().load(deriveConfig[RootConfig]).map(_.config))
      l         <- ZLayer.succeed(appConfig.get.db)
    } yield l

}
