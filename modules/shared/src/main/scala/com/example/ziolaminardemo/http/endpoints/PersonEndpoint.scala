package com.example.ziolaminardemo.http.endpoints

import zio.*
import sttp.tapir.*
import sttp.tapir.json.zio.*
import sttp.tapir.generic.auto.*
import com.example.ziolaminardemo.domain.*
import com.example.ziolaminardemo.login.LoginPassword
import sttp.tapir.EndpointIO.Example

object PersonEndpoint extends BaseEndpoint:

  val create: PublicEndpoint[Person, Throwable, User, Any] = baseEndpoint
    .tag("person")
    .name("person")
    .post
    .in("person")
    .in(
      jsonBody[Person]
        .description("Person to create")
        .example(
          Person(
            "John",
            "john.doe@foo.bar",
            Password("notsecured"),
            Password("notsecured"),
            42,
            Left(Cat("Fluffy"))
          )
        )
    )
    .out(jsonBody[User])
    .description("Create person")

  val login: PublicEndpoint[LoginPassword, Throwable, UserToken, Any] = baseEndpoint
    .tag("person")
    .name("login")
    .post
    .in("login")
    .in(
      jsonBody[LoginPassword]
    )
    .out(jsonBody[UserToken])
    .description("Login")

  val profile: Endpoint[String, Boolean, Throwable, (User, Option[Pet]), Any] = baseSecuredEndpoint
    .tag("person")
    .name("profile")
    .get
    .in("profile")
    .in(query[Boolean]("withPet").default(false))
    .out(jsonBody[(User, Option[Pet])])
    .description("Get profile")

  val listPets: Endpoint[Unit, PetType, Throwable, List[Pet], Any] = baseEndpoint
    .tag("person")
    .name("pets")
    .get
    .in("pets")
    .in(
      query[PetType]("t")
        .default(PetType.Dog)
        .examples(PetType.values.map(v => Example(v, Some(v.toString), Some(v.toString))).toList)
    )
    .out(jsonBody[List[Pet]])
    .description("List pets")
