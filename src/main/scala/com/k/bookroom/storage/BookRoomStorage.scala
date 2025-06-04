package com.k.bookroom.storage

import zio.Task
import zio.macros.accessible
import zio.stream.ZStream

import java.sql.SQLException
import java.util.UUID

@accessible
trait BookRoomStorage {

  def createRoom(room: RoomRow): Task[RoomRow]

  def updateRoom(id: Long, room: RoomRow): Task[RoomRow]

  def getRoom(id: Long): Task[Option[RoomRow]]

  def allRooms(): Task[List[RoomRow]]

  def deleteRoom(id: Long): Task[Unit]

  def deleteAll(): Task[Unit]

  def findRoomWithBooks(id: Long): Task[Option[(RoomRow, List[BookRow])]]

  def book(b: BookRow): Task[BookRow]

  def unbook(id: Long): Task[Unit]

  def booksByRoom(roomId: Long): Task[List[BookRow]]
}
