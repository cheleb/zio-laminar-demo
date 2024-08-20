package com.example.ziolaminardemo.domain

import zio.json.JsonCodec
import sttp.tapir.Schema
import java.time.ZonedDateTime

case class User(
  id: Option[Long],
  name: String,
  email: String,
  hashedPassword: String,
  age: Int,
//  pet: Either[Cat, Dog],
  creationDate: ZonedDateTime
) derives JsonCodec,
      Schema

case class UserID(id: Long, email: String) derives JsonCodec, Schema
