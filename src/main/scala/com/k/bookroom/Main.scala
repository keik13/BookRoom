package com.k.bookroom

import com.k.bookroom.conf.Configuration
import com.k.bookroom.db.{Db, DbMigrator}
import com.k.bookroom.route.BookRoomServer
import com.k.bookroom.service.BookRoomServiceLive
import com.k.bookroom.storage.BookRoomStorageLive
import zio.{Scope, ZIO, ZIOAppArgs, ZIOAppDefault}

object Main extends ZIOAppDefault {

  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = ZIO
    .serviceWithZIO[BookRoomServer](_.run)
    .provide(
      BookRoomServer.layer,
      BookRoomServiceLive.layer,
      BookRoomStorageLive.layer,
      Configuration.layer,
      Db.dataSourceLayer,
      Db.quillLayer,
      DbMigrator.layer
    )
}
