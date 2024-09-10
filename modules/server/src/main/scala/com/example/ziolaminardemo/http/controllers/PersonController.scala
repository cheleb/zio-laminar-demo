package com.example.ziolaminardemo.http.controllers

import dev.cheleb.ziojwt.SecuredBaseController

import zio.*

import sttp.tapir.server.ServerEndpoint
import sttp.tapir.ztapir.*

import com.example.ziolaminardemo.domain.UserToken
import com.example.ziolaminardemo.domain.UserID
import com.example.ziolaminardemo.http.endpoints.PersonEndpoint
import com.example.ziolaminardemo.service.PersonService
import com.example.ziolaminardemo.service.JWTService
import com.example.ziolaminardemo.domain.errors.NotHostHeaderException

class PersonController private (personService: PersonService, jwtService: JWTService)
    extends BaseController
    with SecuredBaseController[String, UserID](jwtService.verifyToken) {

  val create: ServerEndpoint[Any, Task] = PersonEndpoint.createEndpoint
    .zServerLogic(p => personService.register(p))

  val login: ServerEndpoint[Any, Task] = PersonEndpoint.login.zServerLogic { (host, lp) =>
    for {
      user  <- personService.login(lp.login, lp.password)
      host  <- ZIO.fromOption(host).orElseFail(NotHostHeaderException)
      token <- jwtService.createToken(host, user)
    } yield token
  }

  val profile: ServerEndpoint[Any, Task] = PersonEndpoint.profile.securedServerLogic { userId => details =>
    ZIO.logWarning(s"Getting profile for $userId") *>
      personService.getProfile(userId)
  }

  val routes: List[ServerEndpoint[Any, Task]] =
    List(create, login, profile)
}

object PersonController {
  def makeZIO: URIO[PersonService & JWTService, PersonController] =
    for
      jwtService    <- ZIO.service[JWTService]
      personService <- ZIO.service[PersonService]
    yield new PersonController(personService, jwtService)
}
