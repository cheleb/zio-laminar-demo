package com.example.ziolaminardemo.service

import zio.*
import com.example.ziolaminardemo.domain.*
import io.scalaland.chimney.dsl._
import java.time.Instant
import java.time.ZonedDateTime

trait PersonService {
  def register(person: Person): Task[User]
}

class PersonServiceLive private extends PersonService {
  def register(person: Person): Task[User] =
    ZIO.succeed(person.into[User].withFieldComputed(_.creationDate, _ => ZonedDateTime.now()).transform)
}

object PersonServiceLive {
  val layer: ULayer[PersonService] = ZLayer.succeed(new PersonServiceLive)
}
