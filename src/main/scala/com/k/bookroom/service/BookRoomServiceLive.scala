package com.k.bookroom.service

import com.k.bookroom.api.{BookCreateDto, BookDto, RoomDto, RoomResponse}
import com.k.bookroom.storage.{BookRoomStorage, BookRow, RoomRow}
import com.k.bookroom.util.BookPeriodError
import zio.{Task, URLayer, ZIO, ZLayer}

final case class BookRoomServiceLive(storage: BookRoomStorage) extends BookRoomService {

  override def createRoom(room: RoomDto): Task[RoomResponse] =
    storage.createRoom(RoomRow(0L, room.name, room.seatCount)).map(r => RoomResponse(r.id, r.name, r.seatCount))

  override def updateRoom(id: Long, room: RoomDto): Task[RoomResponse] =
    storage.updateRoom(id, RoomRow(id, room.name, room.seatCount)).map(r => RoomResponse(r.id, r.name, r.seatCount))

  override def getRoom(id: Long): Task[Option[RoomResponse]] =
    storage
      .findRoomWithBooks(id)
      .map(op =>
        op.map { case (r, bs) =>
          RoomResponse(r.id, r.name, r.seatCount, bs.map(b => BookDto(b.id, b.roomId, b.employee, b.beginAt, b.endAt)))
        }
      )

  override def allRooms(): Task[List[RoomResponse]] =
    storage.allRooms().map(l => l.map(r => RoomResponse(r.id, r.name, r.seatCount))).catchAll { e =>
      ZIO.logError(e.getMessage).as(Nil)
    }

  override def deleteRoom(id: Long): Task[Unit] = storage.deleteRoom(id)

  override def book(newBook: BookCreateDto): Task[BookDto] = for {
    books <- storage.booksByRoom(newBook.roomId)
    _     <- ZIO.when(newBook.beginAt.isAfter(newBook.endAt))(
               ZIO.logError("beginAt is after endAt") *> ZIO.fail(BookPeriodError(message = "beginAt is after endAt"))
             )
    _     <- ZIO.when(books.exists(b => b.endAt.isAfter(newBook.beginAt)))(
               ZIO.logError("already booked in this time") *> ZIO.fail(BookPeriodError(message = "already booked in this time"))
             )
    b     <- storage.book(BookRow(0L, newBook.roomId, newBook.employee, newBook.beginAt, newBook.endAt))
  } yield BookDto(b.id, b.roomId, b.employee, b.beginAt, endAt = b.endAt)

  override def unbook(id: Long): Task[Unit] = storage.unbook(id)

  override def booksByRoom(roomId: Long): Task[List[BookDto]] =
    storage.booksByRoom(roomId).map(l => l.map(b => BookDto(b.id, b.roomId, b.employee, b.beginAt, b.endAt)))
}

object BookRoomServiceLive {

  val layer: URLayer[BookRoomStorage, BookRoomService] = ZLayer.fromFunction(BookRoomServiceLive.apply _)

}
