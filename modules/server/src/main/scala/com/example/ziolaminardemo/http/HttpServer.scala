package com.example.ziolaminardemo.http

import zio.*
import zio.http.*
import sttp.tapir.files.*
import sttp.tapir.*

import sttp.tapir.server.ziohttp.*
import sttp.tapir.swagger.bundle.SwaggerInterpreter

import com.example.ziolaminardemo.service.*

object HttpServer extends ZIOAppDefault {

  private val webJarRoutes = staticResourcesGetServerEndpoint[Task]("public")(
    this.getClass.getClassLoader,
    "public"
  )

  private val serrverProgram =
    for {
      _         <- ZIO.succeed(println("Hello world"))
      endpoints <- HttpApi.endpointsZIO
      docEndpoints = SwaggerInterpreter()
                       .fromServerEndpoints(endpoints, "zio-laminar-demo", "1.0.0")
      _ <- Server.serve(
             ZioHttpInterpreter(ZioHttpServerOptions.default)
               .toHttp(webJarRoutes :: endpoints ::: docEndpoints)
           )
    } yield ()

  override def run =
    serrverProgram
      .provide(
        Server.default,
        // Service layers
        PersonServiceLive.layer
      )
}
