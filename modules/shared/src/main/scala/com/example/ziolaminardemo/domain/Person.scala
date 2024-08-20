package com.example.ziolaminardemo.domain

import zio.json.JsonCodec
import sttp.tapir.Schema

case class Person(
  name: String,
  email: String,
  password: String,
  passwordConfirmation: String,
  age: Int,
  pet: Either[Cat, Dog]
  // ,length: Option[Int] = None
) derives JsonCodec,
      Schema
