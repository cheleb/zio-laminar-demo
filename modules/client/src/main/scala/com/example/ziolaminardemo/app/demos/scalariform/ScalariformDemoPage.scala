package com.example.ziolaminardemo.app.demos.scalariform

import be.doeraene.webcomponents.ui5.Button

import com.raquo.laminar.api.L.*

import dev.cheleb.scalamigen.{*, given}

import com.example.ziolaminardemo.domain.*
import com.example.ziolaminardemo.core.ZJS.*

import com.example.ziolaminardemo.http.endpoints.PersonEndpoint

object ScalariformDemoPage:
  def apply() =
    val personVar = Var(Person("Alice aup", 42, Left(Cat("Fluffy"))))
    val userBus   = EventBus[User]()

    div(
      styleAttr := "width: 100%; overflow: hidden;",
      div(
        styleAttr := "width: 600px; float: left;",
        Form.renderVar(personVar)
      ),
      div(
        styleAttr := "width: 100px; float: left; margin-top: 200px;",
        Button(
          "-- Post -->",
          onClick --> { _ =>
            // scalafmt:off

            PersonEndpoint
              .createEndpoint(personVar.now())
              .emitTo(userBus)

            // scalafmt:on

          }
        )
      ),
      div(
        styleAttr := "width: 600px; float: left;",
        h1("Databinding"),
        child.text <-- personVar.signal.map(p => s"$p"),
        h1("Response"),
        child <-- userBus.events.map(renderUser),
        hr(),
        child.text <-- userBus.events.map(_.toString)
      )
    )

  def renderUser(user: User) =
    div(
      h2("User"),
      div(s"Name: ${user.name}"),
      div(s"Age: ${user.age}"),
      div(s"Pet: ${user.pet}"),
      div(s"Creation Date: ${user.creationDate}")
    )
