package com.example.ziolaminardemo.http

import zio.*
import sttp.tapir.server.ServerEndpoint

import controllers.*
import com.example.ziolaminardemo.service.PersonService

//https://tapir.softwaremill.com/en/latest/server/logic.html
object HttpApi {
  private def gatherRoutes(
    controllers: List[BaseController]
  ): List[ServerEndpoint[Any, Task]] =
    controllers.flatMap(_.routes)

  private def makeControllers: URIO[PersonService, List[BaseController]] = for {
    healthController <- HealthController.makeZIO
    personController <- PersonController.makeZIO
  } yield List(healthController, personController)

  val endpointsZIO: URIO[PersonService, List[ServerEndpoint[Any, Task]]] = makeControllers.map(gatherRoutes)
}
