package com.example.ziolaminardemo.http.controllers

import zio.*
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.ztapir.*
import com.example.ziolaminardemo.http.endpoints.PersonEndpoint
import com.example.ziolaminardemo.service.PersonService
import com.example.ziolaminardemo.service.JWTService

class PersonController private (personService: PersonService, jwtService: JWTService) extends BaseController {

  val create: ServerEndpoint[Any, Task] = PersonEndpoint.createEndpoint
    .zServerLogic(p => personService.register(p))

  val login: ServerEndpoint[Any, Task] = PersonEndpoint.login.zServerLogic { lp =>
    for {
      user  <- personService.login(lp.login, lp.password)
      token <- jwtService.createToken(user)
    } yield token
  }

  val routes: List[ServerEndpoint[Any, Task]] =
    List(create, login)
}

object PersonController {
  def makeZIO: URIO[PersonService & JWTService, PersonController] =
    for
      jwtService    <- ZIO.service[JWTService]
      personService <- ZIO.service[PersonService]
    yield new PersonController(personService, jwtService)
}
