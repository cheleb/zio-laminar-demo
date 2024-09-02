package com.example.ziolaminardemo.service

import zio.*

import io.scalaland.chimney.dsl._
import java.time.Instant
import java.time.ZonedDateTime

import com.example.ziolaminardemo.domain.*

import com.example.ziolaminardemo.domain.errors.*

import com.example.ziolaminardemo.repositories.UserRepository
import com.example.ziolaminardemo.login.LoginPassword
import dev.cheleb.ziojwt.Hasher
import java.sql.SQLException

trait PersonService {
  def register(person: Person): Task[User]
  def login(email: String, password: String): Task[User]
}

class PersonServiceLive private (userRepository: UserRepository, jwtService: JWTService) extends PersonService {

  def register(person: Person): Task[User] =
    if person.age < 18 then ZIO.fail(TooYoungException(person.age))
    else
      userRepository
        .create(
          User(
            id = None,
            name = person.name,
            email = person.email,
            hashedPassword = Hasher.generatedHash(person.password.toString),
            age = person.age,
            creationDate = ZonedDateTime.now()
          )
        )
        .catchSome { case e: SQLException =>
          ZIO.logError(s"Error code: ${e.getSQLState} while creating user: ${e.getMessage}")
            *> ZIO.fail(UserAlreadyExistsException())
        }

  override def login(email: String, password: String): Task[User] =
    userRepository
      .findByEmail(email)
      .map {
        _.filter(user => Hasher.validateHash(password, user.hashedPassword))
      }
      .someOrFail(InvalidCredentialsException())

}

object PersonServiceLive {
  val layer: RLayer[UserRepository & JWTService, PersonService] = ZLayer.derive[PersonServiceLive]
}
