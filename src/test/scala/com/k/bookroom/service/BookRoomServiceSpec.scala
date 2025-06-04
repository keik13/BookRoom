package com.k.bookroom.service

import com.k.bookroom.api.{BookCreateDto, RoomDto}
import com.k.bookroom.db.Db
import com.k.bookroom.storage.{BookRoomStorage, BookRoomStorageLive}
import com.k.bookroom.util.BookPeriodError
import io.github.scottweaver.zio.aspect.DbMigrationAspect
import io.github.scottweaver.zio.testcontainers.postgres.ZPostgreSQLContainer
import zio.test.Assertion.{equalTo, fails}
import zio.test.TestAspect.sequential
import zio.test._

import java.time.Instant
import java.time.temporal.ChronoUnit

object BookRoomServiceSpec extends ZIOSpecDefault {

  override def spec: Spec[TestEnvironment, Throwable] = {
    suite("BookRoomService")(
      test("should added Room") {
        for {
          room <- BookRoomService.createRoom(RoomDto(Some("new first"), 5))
        } yield assertTrue(room.books.isEmpty && room.name == Option("new first") && room.seatCount == 5)
      },
      test("should get all Room") {
        for {
          _     <- BookRoomService.createRoom(RoomDto(Some("new first"), 5))
          rooms <- BookRoomService.allRooms()
        } yield assertTrue(rooms.size == 1)
      },
      test("should get new Room") {
        for {
          room <- BookRoomService.createRoom(RoomDto(Some("new first"), 5))
          room <- BookRoomService.getRoom(room.id)
        } yield assertTrue(room.get.books.isEmpty && room.get.name == Option("new first") && room.get.seatCount == 5)
      },
      test("should get new Room with book") {
        for {
          room <- BookRoomService.createRoom(RoomDto(Some("new first"), 5))
          b    <- BookRoomService.book(BookCreateDto(room.id, "employee", Instant.now(), Instant.now().plus(1, ChronoUnit.DAYS)))
          room <- BookRoomService.getRoom(room.id)
        } yield assertTrue(room.get.books.size == 1 && room.get.name == Option("new first") && room.get.seatCount == 5)
      },
      test("should unbook") {
        for {
          room         <- BookRoomService.createRoom(RoomDto(Some("new first"), 5))
          b            <- BookRoomService.book(BookCreateDto(room.id, "employee", Instant.now(), Instant.now().plus(1, ChronoUnit.DAYS)))
          bookedRoom   <- BookRoomService.getRoom(room.id)
          _            <- BookRoomService.unbook(b.id)
          unbookedRoom <- BookRoomService.getRoom(room.id)
        } yield assertTrue(
          bookedRoom.get.books.size == 1 && bookedRoom.get.name == Option(
            "new first"
          ) && bookedRoom.get.seatCount == 5 && unbookedRoom.get.books.isEmpty
        )
      },
      test("should get books") {
        for {
          room  <- BookRoomService.createRoom(RoomDto(Some("new first"), 5))
          b     <- BookRoomService.book(BookCreateDto(room.id, "employee", Instant.now(), Instant.now().plus(1, ChronoUnit.HOURS)))
          b     <- BookRoomService.book(
                     BookCreateDto(room.id, "employee2", Instant.now().plus(2, ChronoUnit.HOURS), Instant.now().plus(3, ChronoUnit.HOURS))
                   )
          books <- BookRoomService.booksByRoom(room.id)
        } yield assertTrue(books.size == 2)
      },
      test("should get books") {
        for {
          room  <- BookRoomService.createRoom(RoomDto(Some("new first"), 5))
          b     <- BookRoomService.book(BookCreateDto(room.id, "employee", Instant.now(), Instant.now().plus(1, ChronoUnit.HOURS)))
          b     <- BookRoomService.book(
                     BookCreateDto(room.id, "employee2", Instant.now().plus(2, ChronoUnit.HOURS), Instant.now().plus(3, ChronoUnit.HOURS))
                   )
          books <- BookRoomService.booksByRoom(room.id)
        } yield assertTrue(books.size == 2)
      },
      test("should error with start after end") {
        val testCase = for {
          room <- BookRoomService.createRoom(RoomDto(Some("new first"), 5))
          b    <- BookRoomService.book(BookCreateDto(room.id, "employee", Instant.now(), Instant.now().minus(1, ChronoUnit.HOURS)))
        } yield assertTrue(true)
        assertZIO(testCase.exit)(
          fails(equalTo(BookPeriodError(message = "beginAt is after endAt")))
        )
      },
      test("should error with intersecting books") {
        val testCase = for {
          room <- BookRoomService.createRoom(RoomDto(Some("new first"), 5))
          b    <- BookRoomService.book(BookCreateDto(room.id, "employee", Instant.now(), Instant.now().plus(1, ChronoUnit.HALF_DAYS)))
          b    <- BookRoomService.book(
                    BookCreateDto(room.id, "employee2", Instant.now().plus(1, ChronoUnit.HOURS), Instant.now().plus(1, ChronoUnit.HALF_DAYS))
                  )
        } yield assertTrue(true)
        assertZIO(testCase.exit)(
          fails(equalTo(BookPeriodError(message = "already booked in this time")))
        )
      },
      test("should delete new Room") {
        for {
          room <- BookRoomService.createRoom(RoomDto(Some("new first"), 5))
          _    <- BookRoomService.deleteRoom(room.id)
          room <- BookRoomService.getRoom(room.id)
        } yield assertTrue(room.isEmpty)
      },
      test("should update new Room") {
        for {
          room <- BookRoomService.createRoom(RoomDto(Some("new first"), 5))
          room <- BookRoomService.updateRoom(room.id, RoomDto(Some("new second"), 6))
        } yield assertTrue(room.books.isEmpty && room.name == Option("new second") && room.seatCount == 6)
      }
    ) @@ DbMigrationAspect.migrateOnce()() @@ TestAspect.after(BookRoomStorage.deleteAll())
  }
    .provideShared(
      BookRoomServiceLive.layer,
      BookRoomStorageLive.layer,
      Db.quillLayer,
      ZPostgreSQLContainer.live,
      ZPostgreSQLContainer.Settings.default
    ) @@ sequential
}
