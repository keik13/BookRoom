package com.k.bookroom.server

import com.k.bookroom.conf.Configuration
import com.k.bookroom.db.{Db, DbMigrator}
import com.k.bookroom.route.BookRoomServer
import com.k.bookroom.service.BookRoomServiceLive
import com.k.bookroom.storage.{BookRoomStorage, BookRoomStorageLive, RoomRow}
import io.github.scottweaver.zio.aspect.DbMigrationAspect
import io.github.scottweaver.zio.testcontainers.postgres.ZPostgreSQLContainer
import zio.http.{Body, Request, Status, URL}
import zio.test._
import zio.{durationInt, Scope, ZIO}

object BookRoomServerSpec extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment, Any] = {
    suite("BookRoomServer")(
      test("should ok POST /room") {
        for {
          server <- ZIO.service[BookRoomServer]
          r      <- server.roomRoute(Request.post("/room", Body.fromString("{\"name\":\"first\", \"seatCount\":4}")))
        } yield assertTrue(r.status == Status.Ok)
      }
    ) @@ DbMigrationAspect.migrateOnce()()
  }
    .provideShared(
      BookRoomServer.layer,
      BookRoomServiceLive.layer,
      BookRoomStorageLive.layer,
      Db.quillLayer,
      DbMigrator.layer,
      ZPostgreSQLContainer.Settings.default,
      ZPostgreSQLContainer.live,
      Scope.default
    )
}
