package com.example.ziolaminardemo.service

import zio.*

import dev.cheleb.ziojwt.Hasher

import io.scalaland.chimney.dsl._
import java.time.Instant
import java.time.ZonedDateTime

import com.example.ziolaminardemo.domain.*
import com.example.ziolaminardemo.domain.errors.*
import com.example.ziolaminardemo.login.LoginPassword
import com.example.ziolaminardemo.repositories.UserRepository
import com.example.ziolaminardemo.UserEntity
import com.example.ziolaminardemo.NewUserEntity
import java.sql.SQLException
import io.getquill.jdbczio.Quill
import io.getquill.SnakeCase
import com.example.ziolaminardemo.repositories.TransactionSupport
import io.getquill.jdbczio.Quill.Postgres
import com.example.ziolaminardemo.repositories.PetRepository
import com.example.ziolaminardemo.PetEntity

import com.example.ziolaminardemo.CatEntity
import com.example.ziolaminardemo.DogEntity
import io.scalaland.chimney.Transformer
trait PersonService {
  def register(person: Person): Task[User]
  def login(email: String, password: String): Task[User]
  def getProfile(userId: UserID, withPet: Boolean): Task[(User, Option[Pet])]
}

class PersonServiceLive private (
  userRepository: UserRepository,
  petRepository: PetRepository,
  jwtService: JWTService,
  quill: Quill.Postgres[SnakeCase]
) extends PersonService
    with TransactionSupport(quill) {

  def register(person: Person): Task[User] =
    if person.age < 18 then ZIO.fail(TooYoungException(person.age))
    else
      tx(
        for {
          _ <- ZIO.logDebug(s"Registering user: $person")
          newPetEntity = person.pet match
                           case Right(value) => PetEntity(value)
                           case Left(value)  => PetEntity(value)
          petEntity <- petRepository.create(newPetEntity)
          user <- userRepository
                    .create(
                      NewUserEntity(
                        None,
                        name = person.name,
                        email = person.email,
                        hashedPassword = Hasher.generatedHash(person.password.toString),
                        petType = Some(person.pet.fold(_ => PetType.Cat, _ => PetType.Dog)),
                        petId = Some(petEntity.id),
                        age = person.age,
                        creationDate = ZonedDateTime.now()
                      )
                    )
                    .catchSome { case e: SQLException =>
                      ZIO.logError(s"Error code: ${e.getSQLState} while creating user: ${e.getMessage}")
                        *> ZIO.fail(UserAlreadyExistsException())
                    }
                    .mapInto[User]
        } yield user
      )
  override def login(email: String, password: String): Task[User] =
    userRepository
      .findByEmail(email)
      .map {
        _.filter(user => Hasher.validateHash(password, user.hashedPassword))
      }
      .someOrFail(InvalidCredentialsException())
      .mapInto[User]

  override def getProfile(userId: UserID, withPet: Boolean): Task[(User, Option[Pet])] =
    for
      userEntity <- userRepository
                      .findByEmail(userId.email)
                      .someOrFail(UserNotFoundException(userId.email))
      user = userEntity.into[User].transform
      pet <- if (withPet)
               (userEntity.petType, userEntity.petId) match {
                 case (Some(petType), Some(petId)) =>
                   petType match
                     case PetType.Cat =>
                       petRepository
                         .getCatById(petId)
                         .mapOption[Cat]

                     case PetType.Dog =>
                       petRepository
                         .getDogById(petId)
                         .mapOption[Dog]

                 case _ => ZIO.none
               }
             else ZIO.none
    yield (user, pet)

}

object PersonServiceLive {
  val layer: RLayer[UserRepository & PetRepository & JWTService & Postgres[SnakeCase], PersonService] =
    ZLayer.derive[PersonServiceLive]
}
