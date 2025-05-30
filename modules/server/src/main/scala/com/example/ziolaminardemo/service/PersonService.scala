package com.example.ziolaminardemo.service

import zio.*

import io.scalaland.chimney.dsl._
import java.time.ZonedDateTime

import com.example.ziolaminardemo.domain.*
import com.example.ziolaminardemo.domain.errors.*
import com.example.ziolaminardemo.repositories.UserRepository
import com.example.ziolaminardemo.UserEntity
import com.example.ziolaminardemo.NewUserEntity
import com.example.ziolaminardemo.repositories.TransactionSupport
import com.example.ziolaminardemo.repositories.PetRepository
import com.example.ziolaminardemo.PetEntity
import com.example.ziolaminardemo.CatEntity
import com.example.ziolaminardemo.DogEntity

import java.sql.SQLException

import io.getquill.jdbczio.Quill
import io.getquill.SnakeCase
import io.getquill.jdbczio.Quill.Postgres

trait PersonService {
  def register(person: Person): Task[User]
  def login(email: String, password: String): Task[User]
  def getProfile(userId: UserID, withPet: Boolean): Task[(User, Option[Pet])]
  def listPets(petType: PetType): Task[List[Pet]]
}

class PersonServiceLive private (
  userRepository: UserRepository,
  petRepository: PetRepository,
  quill: Quill.Postgres[SnakeCase]
) extends PersonService
    with TransactionSupport(quill) {

  override def listPets(petType: PetType): Task[List[Pet]] =
    petType match
      case PetType.Cat =>
        petRepository
          .getAllCats()
          .map(_.map(_.into[Cat].transform))
      case PetType.Dog =>
        petRepository
          .getAllDogs()
          .map(_.map(_.into[Dog].transform))

  def register(person: Person): Task[User] =
    if person.age < 18 then ZIO.fail(TooYoungException(person.age))
    else
      tx(
        for {
          _           <- ZIO.logDebug(s"Registering user: $person")
          newPetEntity = person.pet.fold(PetEntity.apply, PetEntity.apply)
          petEntity   <- petRepository.create(newPetEntity)
          user        <- userRepository
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
      pet <- maybePet(userEntity, withPet)
    yield (user, pet)

  private def maybePet(userEntity: UserEntity, withPet: Boolean): Task[Option[Pet]] =
    if withPet then
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

}

object PersonServiceLive {
  val layer: RLayer[UserRepository & PetRepository & Postgres[SnakeCase], PersonService] =
    ZLayer.derive[PersonServiceLive]
}
