package com.example.ziolaminardemo.domain

import zio.json.JsonCodec
import sttp.tapir.Schema
import java.time.ZonedDateTime

case class User(
  name: String,
  age: Int,
  pet: Either[Cat, Dog],
  creationDate: ZonedDateTime
//  length: Option[Int] = None
) derives JsonCodec,
      Schema
