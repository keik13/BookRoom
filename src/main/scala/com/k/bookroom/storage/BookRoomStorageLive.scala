package com.k.bookroom.storage

import com.k.bookroom.storage.BookRoomStorageLive.leftJoinOneToMany
import io.getquill.SnakeCase
import io.getquill.jdbczio.Quill
import zio.{Task, URLayer, ZLayer}

import java.time.Instant

case class RoomRow(
  id: Long,
  name: Option[String],
  seatCount: Int
)

case class BookRow(
  id: Long,
  roomId: Long,
  employee: String,
  beginAt: Instant,
  endAt: Instant
)

final case class BookRoomStorageLive(quill: Quill.Postgres[SnakeCase]) extends BookRoomStorage {

  import quill._

  private val queryRoom = quote(querySchema[RoomRow](entity = "room"))
  private val queryBook = quote(querySchema[BookRow](entity = "book"))

  override def createRoom(room: RoomRow): Task[RoomRow] = run(
    queryRoom
      .insert(_.name -> lift(room.name), _.seatCount -> lift(room.seatCount))
      .returning(r => r)
  )

  override def deleteAll(): Task[Unit] = run(queryRoom.delete).unit

  override def updateRoom(id: Long, room: RoomRow): Task[RoomRow] = run(
    queryRoom.filter(_.id == lift(id)).update(_.name -> lift(room.name), _.seatCount -> lift(room.seatCount)).returning(r => r)
  )

  override def getRoom(id: Long): Task[Option[RoomRow]] = run(queryRoom.filter(_.id == lift(id))).map(_.headOption)

  override def allRooms(): Task[List[RoomRow]] = run(queryRoom)

  override def deleteRoom(id: Long): Task[Unit] = run(queryRoom.filter(_.id == lift(id)).delete).unit

  override def findRoomWithBooks(
    id: Long
  ): Task[Option[(RoomRow, List[BookRow])]] =
    run(
      queryRoom
        .leftJoin(queryBook)
        .on(_.id == _.roomId)
        .filter(_._1.id == lift(id))
    ).map(leftJoinOneToMany(_).headOption)

  override def book(b: BookRow): Task[BookRow] = run(
    queryBook
      .insert(_.employee -> lift(b.employee), _.roomId -> lift(b.roomId), _.beginAt -> lift(b.beginAt), _.endAt -> lift(b.endAt))
      .returning(b => b)
  )

  override def unbook(id: Long): Task[Unit] = run(queryBook.filter(_.id == lift(id)).delete).unit

  override def booksByRoom(roomId: Long): Task[List[BookRow]] = run(queryBook.filter(_.roomId == lift(roomId)))

}

object BookRoomStorageLive {

  val layer: URLayer[Quill.Postgres[SnakeCase], BookRoomStorage] = ZLayer.fromFunction(BookRoomStorageLive.apply _)

  def leftJoinOneToMany[R, R1](
    informExchangeRow: List[(R, Option[R1])]
  ): List[(R, List[R1])] =
    informExchangeRow.groupMap { case (iep, _) => iep } { case (_, data) => data }.view.mapValues(_.flatten).toList

}
