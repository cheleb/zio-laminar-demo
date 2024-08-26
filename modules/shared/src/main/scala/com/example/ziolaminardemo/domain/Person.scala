package com.example.ziolaminardemo.domain

import zio.json.JsonCodec
import sttp.tapir.Schema

case class Person(
  name: String,
  email: String,
  password: Password,
  passwordConfirmation: Password,
  age: Int,
  pet: Either[Cat, Dog]
  // ,length: Option[Int] = None
) derives JsonCodec,
      Schema

opaque type Password = String

object Password:
  given JsonCodec[Password] = JsonCodec.string
  given Schema[Password]    = Schema.string

  def apply(password: String): Password = password
