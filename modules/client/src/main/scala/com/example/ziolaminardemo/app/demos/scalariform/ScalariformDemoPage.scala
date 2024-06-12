package com.example.ziolaminardemo.app.demos.scalariform

import be.doeraene.webcomponents.ui5.Button

import com.raquo.laminar.api.L.*

import dev.cheleb.scalamigen.{*, given}

import com.example.ziolaminardemo.domain.*
import com.example.ziolaminardemo.core.ZJS.*

object ScalariformDemoPage:
  def apply() =
    val personVar = Var(Person("Alice", 42, Left(Cat("Fluffy"))))
    val userBus   = EventBus[User]()

    div(
      styleAttr := "width: 100%; overflow: hidden;",
      div(
        styleAttr := "width: 600px; float: left;",
        h1("Hello World"),
        child.text <-- personVar.signal.map(p => s"$p"),
        Form.renderVar(personVar)
      ),
      div(
        styleAttr := "width: 60px; float: left; margin-top: 200px;",
        Button(
          "Post",
          onClick --> { _ =>
            println(personVar.now())
            useBackend(_.person.createEndpoint(personVar.now())).emitTo(userBus)
          }
        )
      ),
      div(
        styleAttr := "width: 600px; float: left;",
        h1("Response"),
        child <-- userBus.events.map(renderUser)
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
