package com.example.ziolaminardemo.domain

import zio.json.JsonCodec
import sttp.tapir.Schema
import zio.prelude.*
import zio.prelude.Debug.Repr
import zio.prelude.Debug.Renderer

import zio.prelude.magnolia.*
import dev.cheleb.scalamigen.NoPanel

@NoPanel(asTable = false)
case class Person(
  name: String,
  email: String,
  password: Password,
  passwordConfirmation: Password,
  age: Int,
  pet: Either[Cat, Dog],
  op: Option[String] = None
) derives JsonCodec,
      Schema,
      Debug

opaque type Password <: String = String

object Password:
  given JsonCodec[Password] = JsonCodec.string
  given Schema[Password]    = Schema.string

  given Debug[Password] with
    def debug(value: Password): Repr = Repr.String("*****")

  def apply(password: String): Password = password
