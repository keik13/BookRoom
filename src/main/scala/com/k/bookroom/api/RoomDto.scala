package com.k.bookroom.api

import zio.json.{jsonNoExtraFields, DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

@jsonNoExtraFields
case class RoomDto(name: Option[String], seatCount: Int)

object RoomDto {
  implicit val decoder: JsonDecoder[RoomDto] = DeriveJsonDecoder.gen[RoomDto]
}

@jsonNoExtraFields
case class RoomResponse(id: Long, name: Option[String], seatCount: Int, books: List[BookDto] = Nil)

object RoomResponse {
  implicit val encoder: JsonEncoder[RoomResponse] = DeriveJsonEncoder.gen[RoomResponse]
}
