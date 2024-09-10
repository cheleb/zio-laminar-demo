package com.example.ziolaminardemo.http.endpoints

import sttp.tapir.*
import zio.*
import sttp.tapir.*
import sttp.tapir.json.zio.*
import sttp.tapir.generic.auto.*
import com.example.ziolaminardemo.domain.*
import com.example.ziolaminardemo.login.LoginPassword
import sttp.model.Headers
import sttp.model.Header
import sttp.model.HeaderNames

object PersonEndpoint extends BaseEndpoint:

  val createEndpoint: Endpoint[Unit, Person, Throwable, User, Any] = baseEndpoint
    .tag("person")
    .name("person")
    .post
    .in("person")
    .in(
      jsonBody[Person]
        .description("Person to create")
        .example(
          Person("John", "john.doe@foo.bar", Password("notsecured"), Password("notsecured"), 42, Left(Cat("Fluffy")))
        )
    )
    .out(jsonBody[User])
    .description("Create person")

  val login: Endpoint[Unit, (Option[String], LoginPassword), Throwable, UserToken, Any] = baseEndpoint
    .tag("person")
    .name("login")
    .post
    .in(header[Option[String]](HeaderNames.Host))
    .in("login")
    .in(
      jsonBody[LoginPassword]
    )
    .out(jsonBody[UserToken])
    .description("Login")

  val profile: Endpoint[String, Unit, Throwable, User, Any] = baseSecuredEndpoint
    .tag("person")
    .name("profile")
    .get
    .in("profile")
//    .in(query[Boolean]("details"))
    .out(jsonBody[User])
    .description("Get profile")
