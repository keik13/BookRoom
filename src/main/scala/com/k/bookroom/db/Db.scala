package com.k.bookroom.db

import com.k.bookroom.conf.DbConfig
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import io.getquill.SnakeCase
import io.getquill.jdbczio.Quill
import zio.{URLayer, ZIO, ZLayer}

import javax.sql.DataSource

object Db {
  private def create(dbConfig: DbConfig): HikariDataSource = {
    val poolConfig = new HikariConfig()
    poolConfig.setJdbcUrl(dbConfig.url)
    poolConfig.setUsername(dbConfig.user)
    poolConfig.setPassword(dbConfig.password)
    new HikariDataSource(poolConfig)
  }

  // Used for migration and executing queries.
  val dataSourceLayer: URLayer[DbConfig, DataSource] =
    ZLayer.scoped {
      ZIO.fromAutoCloseable {
        for {
          dbConfig   <- ZIO.service[DbConfig]
          dataSource <- ZIO.succeed(create(dbConfig))
        } yield dataSource
      }
    }

  val quillLayer: URLayer[DataSource, Quill.Postgres[SnakeCase]] = Quill.Postgres.fromNamingStrategy(SnakeCase)
}
