package com.k.bookroom.route

import com.k.bookroom.api.{BookCreateDto, RoomDto}
import com.k.bookroom.db.DbMigrator
import com.k.bookroom.route.BookRoomServer.parseBody
import com.k.bookroom.service.BookRoomService
import zio.http.{long, Handler, Method, Request, Response, Root, Routes, Server}
import zio.json.{DecoderOps, EncoderOps, JsonDecoder}
import zio.{&, IO, Task, URLayer, ZIO, ZLayer}

final case class BookRoomServer(service: BookRoomService, migrator: DbMigrator) {

  val roomRoute =
    Routes(
      Method.POST / Root / "room"                -> Handler.fromFunctionZIO[Request] { request =>
        for {
          dto <- parseBody[RoomDto](request)
          r   <- service.createRoom(dto)
        } yield Response.json(r.toJson)
      },
      Method.POST / Root / "room" / long("id")   -> Handler.fromFunctionZIO[(Long, Request)] { case (id, request) =>
        for {
          dto <- parseBody[RoomDto](request)
          r   <- service.updateRoom(id, dto)
        } yield Response.json(r.toJson)
      },
      Method.GET / Root / "room"                 -> Handler.fromFunctionZIO[Request] { _ =>
        for {
          r <- service.allRooms()
        } yield Response.json(r.toJson)
      },
      Method.GET / Root / "room" / long("id")    -> Handler.fromFunctionZIO[(Long, Request)] { case (id, _) =>
        for {
          r <- service.getRoom(id)
        } yield Response.json(r.toJson)
      },
      Method.DELETE / Root / "room" / long("id") -> Handler.fromFunctionZIO[(Long, Request)] { case (id, _) =>
        for {
          _ <- service.deleteRoom(id)
        } yield Response.ok
      }
    ).sandbox

  val bookRoute =
    Routes(
      Method.POST / Root / "book"                      -> Handler.fromFunctionZIO[Request] { request =>
        for {
          dto <- parseBody[BookCreateDto](request)
          r   <- service.book(dto)
        } yield Response.json(r.toJson)
      },
      Method.GET / Root / "room" / long("id") / "book" -> Handler.fromFunctionZIO[(Long, Request)] { case (id, _) =>
        for {
          r <- service.booksByRoom(id)
        } yield Response.json(r.toJson)
      },
      Method.DELETE / Root / "book" / long("id")       -> Handler.fromFunctionZIO[(Long, Request)] { case (id, _) =>
        for {
          _ <- service.unbook(id)
        } yield Response.ok
      }
    ).sandbox

  def run: Task[Unit] = for {
    _ <- migrator.migrate
    _ <- Server
           .serve(roomRoute ++ bookRoute)
           .provide(Server.defaultWith { config =>
             config.disableRequestStreaming(1024 * 1024)
           })
  } yield ()

}

object BookRoomServer {
  val layer: URLayer[BookRoomService & DbMigrator, BookRoomServer] = ZLayer.fromFunction(BookRoomServer.apply _)

  def parseBody[A: JsonDecoder](request: Request): IO[String, A] = (for {
    body <- request.body.asString.mapError(_.getMessage)
    dto  <- ZIO.fromEither(body.fromJson[A])
  } yield dto).tapError(e => ZIO.logError(e))

}
