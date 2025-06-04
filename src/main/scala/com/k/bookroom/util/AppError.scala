package com.k.bookroom.util

import zio.http.Status

sealed trait AppError extends Throwable {
  def status: Status
  def message: String
}

case class BookPeriodError(status: Status = Status.BadRequest, message: String) extends AppError
