package com.example.ziolaminardemo.http

import zio.*
import zio.http.*
import sttp.tapir.files.*
import sttp.tapir.*

import sttp.tapir.server.ziohttp.*
import sttp.tapir.swagger.bundle.SwaggerInterpreter

import com.example.ziolaminardemo.service.*

import com.example.ziolaminardemo.http.controllers.PrometheusController

object HttpServer extends ZIOAppDefault {

  private val webJarRoutes = staticResourcesGetServerEndpoint[Task]("public")(
    this.getClass.getClassLoader,
    "public"
  )

  private val serrverProgram =
    val serverOptions: ZioHttpServerOptions[Any] =
      ZioHttpServerOptions.customiseInterceptors
        .metricsInterceptor(PrometheusController.prometheusMetrics.metricsInterceptor())
        .options
    for {
      _         <- ZIO.succeed(println("Hello world"))
      endpoints <- HttpApi.endpointsZIO
      docEndpoints = SwaggerInterpreter()
                       .fromServerEndpoints(endpoints, "zio-laminar-demo", "1.0.0")
      _ <- Server.serve(
             ZioHttpInterpreter(serverOptions)
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
