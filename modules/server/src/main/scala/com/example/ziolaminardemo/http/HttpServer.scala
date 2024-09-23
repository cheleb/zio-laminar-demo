package com.example.ziolaminardemo.http

import zio.*
import zio.http.*

import sttp.tapir.*
import sttp.tapir.files.*
import sttp.tapir.server.ziohttp.*
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import sttp.tapir.server.interceptor.cors.CORSInterceptor

import com.example.ziolaminardemo.service.*
import com.example.ziolaminardemo.http.prometheus.*
import com.example.ziolaminardemo.services.FlywayService
import com.example.ziolaminardemo.services.FlywayServiceLive
import com.example.ziolaminardemo.repositories.UserRepositoryLive
import com.example.ziolaminardemo.repositories.Repository
import com.example.ziolaminardemo.repositories.PetRepositoryLive

object HttpServer extends ZIOAppDefault {

  private val webJarRoutes = staticResourcesGetServerEndpoint[Task]("public")(
    this.getClass.getClassLoader,
    "public"
  )

  val serverOptions: ZioHttpServerOptions[Any] =
    ZioHttpServerOptions.customiseInterceptors
      .metricsInterceptor(metricsInterceptor)
      .appendInterceptor(
        CORSInterceptor.default
      )
      .options

  val runMigrations = for {
    flyway <- ZIO.service[FlywayService]
    _ <- flyway.runMigrations().catchSome { case e =>
           ZIO.logError(s"Error running migrations: ${e.getMessage()}")
             *> flyway.runRepair() *> flyway.runMigrations()
         }
  } yield ()

  private val server =
    for {
      _            <- Console.printLine("Starting server...")
      apiEndpoints <- HttpApi.endpointsZIO
      docEndpoints = SwaggerInterpreter()
                       .fromServerEndpoints(apiEndpoints, "zio-laminar-demo", "1.0.0")
      _ <- Server.serve(
             Routes(
               Method.GET / Root -> handler(Response.redirect(url"public/index.html"))
             ) ++
               ZioHttpInterpreter(serverOptions)
                 .toHttp(metricsEndpoint :: webJarRoutes :: apiEndpoints ::: docEndpoints)
           )
    } yield ()

  private val program =
    for {
      _ <- runMigrations
      _ <- server
    } yield ()

  override def run =
    program
      .provide(
        Server.default,
        // Service layers
        PersonServiceLive.layer,
        FlywayServiceLive.configuredLayer,
        JWTServiceLive.configuredLayer,
        // Repository layers
        UserRepositoryLive.layer,
        PetRepositoryLive.layer,
        Repository.dataLayer
      )
}
