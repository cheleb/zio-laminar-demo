package com.example.ziolaminardemo.app.signup

import zio.prelude.*

import be.doeraene.webcomponents.ui5.*

import com.raquo.laminar.api.L.*

import dev.cheleb.scalamigen.*

import dev.cheleb.ziolaminartapir.*

import com.example.ziolaminardemo.http.endpoints.PersonEndpoint
import be.doeraene.webcomponents.ui5.configkeys.ToastPlacement

import scala.concurrent.duration.DurationInt

import com.example.ziolaminardemo.app.given
import com.example.ziolaminardemo.domain.*

object SignupPage:
  def apply() =
    val personVar = Var(
      Person("John", "john.does@foo.bar", Password("notsecured"), Password("notsecured"), 42, Left(Cat("Fluffy")))
    )
    val userBus  = EventBus[User]()
    val errorBus = EventBus[Throwable]()

    val debugVar = Var(false)

    div(
      styleAttr := "max-width: fit-content;  margin-left: auto;  margin-right: auto;",
      h1("Signup"),
      div(
        styleAttr := "width: 600px; float: left;",
        personVar.asForm,
        child.maybe <-- personVar.signal.map {
          case Person(_, _, password, passwordConfirmation, _, _, _) if password != passwordConfirmation =>
            Some(div("Passwords do not match"))
          case _ => None
        }
      ),
      div(
        styleAttr := "float: right;",
        Switch(
          _.textOn  := "🔎",
          _.textOff := "🔎",
          _.tooltip := "On/Off Switch",
          onChange.mapToChecked --> { checked =>
            debugVar.set(checked)
          }
        ),
        div(
          styleAttr := "float: both;",
          child <-- debugVar.signal.map:
            case true =>
              div(
                styleAttr := "max-width: 300px; margin:1em auto",
                Title("Databinding"),
                child.text <-- personVar.signal.map(p => s"${p.render}")
              )
            case false => div()
        )
      ),
      div(
        styleAttr := "clear:both;max-width: fit-content; margin:1em auto",
        Button(
          "Create",
          onClick --> { _ =>
            // scalafmt:off

            PersonEndpoint
              .createEndpoint(personVar.now())
              .emitTo(userBus, errorBus)

            // scalafmt:on

          }
        )
      ),
      Toast(
        cls := "srf-valid",
        _.duration  := 2.seconds,
        _.placement := ToastPlacement.MiddleCenter,
        child <-- userBus.events.map(renderUser),
        _.open <-- userBus.events.map(_ => true)
      ),
      Toast(
        cls := "srf-invalid",
        _.duration  := 2.seconds,
        _.placement := ToastPlacement.MiddleCenter,
        child <-- errorBus.events.map(_.getMessage()),
        _.open <-- errorBus.events.map(_ => true)
      )
    )

  def renderUser(user: User) =
    div(
      h2("User"),
      div(s"Id: ${user.id}"),
      div(s"Name: ${user.name}"),
      div(s"Age: ${user.age}"),
      div(s"Creation Date: ${user.creationDate}")
    )
