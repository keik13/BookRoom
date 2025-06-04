package com.k.bookroom.api

import zio.json.{jsonNoExtraFields, DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

import java.time.Instant

@jsonNoExtraFields
case class BookCreateDto(roomId: Long, employee: String, beginAt: Instant, endAt: Instant)

object BookCreateDto {
  implicit val decoder: JsonDecoder[BookCreateDto] = DeriveJsonDecoder.gen[BookCreateDto]
  implicit val encoder: JsonEncoder[BookCreateDto] = DeriveJsonEncoder.gen[BookCreateDto]
}

@jsonNoExtraFields
case class BookDto(id: Long, roomId: Long, employee: String, beginAt: Instant, endAt: Instant)

object BookDto {
  implicit val decoder: JsonDecoder[BookDto] = DeriveJsonDecoder.gen[BookDto]
  implicit val encoder: JsonEncoder[BookDto] = DeriveJsonEncoder.gen[BookDto]
}
