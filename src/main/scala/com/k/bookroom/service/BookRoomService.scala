package com.k.bookroom.service

import com.k.bookroom.api.{BookCreateDto, BookDto, RoomDto, RoomResponse}
import zio.Task
import zio.macros.accessible

@accessible
trait BookRoomService {

  def createRoom(room: RoomDto): Task[RoomResponse]

  def updateRoom(id: Long, room: RoomDto): Task[RoomResponse]

  def getRoom(id: Long): Task[Option[RoomResponse]]

  def allRooms(): Task[List[RoomResponse]]

  def deleteRoom(id: Long): Task[Unit]

  def book(b: BookCreateDto): Task[BookDto]

  def unbook(id: Long): Task[Unit]

  def booksByRoom(roomId: Long): Task[List[BookDto]]

}
