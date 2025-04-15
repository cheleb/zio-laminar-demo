package com.example.ziolaminardemo.http.controllers

import dev.cheleb.ziotapir.SecuredBaseController

import zio.*

import sttp.tapir.server.ServerEndpoint
import sttp.tapir.ztapir.*

import com.example.ziolaminardemo.domain.*
import com.example.ziolaminardemo.http.endpoints.PersonEndpoint
import com.example.ziolaminardemo.service.PersonService
import com.example.ziolaminardemo.service.JWTService

class PersonController private (personService: PersonService, jwtService: JWTService)
    extends SecuredBaseController[String, UserID](jwtService.verifyToken) {

  val create: ServerEndpoint[Any, Task] = PersonEndpoint.create
    .zServerLogic:
      personService.register

  val login: ServerEndpoint[Any, Task] = PersonEndpoint.login.zServerLogic { lp =>
    for {
      user  <- personService.login(lp.login, lp.password)
      token <- jwtService.createToken(user)
    } yield token
  }

  val profile: ServerEndpoint[Any, Task] = PersonEndpoint.profile.zServerAuthenticatedLogic { userId => withPet =>
    personService.getProfile(userId, withPet)
  }

  val listPets: ServerEndpoint[Any, Task] = PersonEndpoint.listPets.zServerLogic { petType =>
    personService.listPets(petType)
  }

  override val routes: List[ServerEndpoint[Any, Task]] = List(create, login, profile, listPets)
}

object PersonController {
  def makeZIO: URIO[PersonService & JWTService, PersonController] =
    for
      jwtService    <- ZIO.service[JWTService]
      personService <- ZIO.service[PersonService]
    yield new PersonController(personService, jwtService)
}
