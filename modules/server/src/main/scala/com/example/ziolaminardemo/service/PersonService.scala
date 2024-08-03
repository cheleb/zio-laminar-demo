package com.example.ziolaminardemo.service

import zio.*

import io.scalaland.chimney.dsl._
import java.time.Instant
import java.time.ZonedDateTime

import com.example.ziolaminardemo.domain.*

import com.example.ziolaminardemo.repositories.UserRepository

trait PersonService {
  def register(person: Person): Task[User]
}

class PersonServiceLive private (userRepository: UserRepository) extends PersonService {
  def register(person: Person): Task[User] =
    userRepository.create(
      User(
        id = None,
        name = person.name,
        email = person.email,
        age = person.age,
        creationDate = ZonedDateTime.now()
      )
    )
}

object PersonServiceLive {
  val layer: RLayer[UserRepository, PersonService] = ZLayer.derive[PersonServiceLive]
}
