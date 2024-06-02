package com.example.ziolaminardemo.app.demos.scalariform

import com.raquo.laminar.api.L.*

import dev.cheleb.scalamigen.{*, given}

import com.example.ziolaminardemo.domain.*
import com.example.ziolaminardemo.core.*
import com.example.ziolaminardemo.core.ZJS.*

object ScalariformDemoPage:
  def apply() =
    val personVar = Var(Person("Alice", 42, Left(Cat("Fluffy"))))
    div(
      h1("Hello World"),
      child.text <-- personVar.signal.map(p => s"$p"),
      Form.renderVar(personVar),
      hr(),
      button(
        "Post",
        onClick --> { _ =>
          println(personVar.now())
          useBackend(_.person.createEndpoint(personVar.now())).runJs
        }
      )
    )
