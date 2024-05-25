package com.example.ziolaminardemo.http.controllers

import zio.*
import sttp.tapir.*
import sttp.tapir.server.ServerEndpoint

class HealthController private extends BaseController with com.example.ziolaminardemo.http.endpoints.HealthEndpoint {

  val health = healthEndpoint
    .serverLogicSuccess[Task](_ => ZIO.succeed("OK"))
  override val routes: List[ServerEndpoint[Any, Task]] = List(health)
}

object HealthController {
  val makeZIO = ZIO.succeed(new HealthController)
}
