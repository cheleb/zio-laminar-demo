package com.example.ziolaminardemo.app.demos

import com.raquo.laminar.api.L.*

import com.example.ziolaminardemo.app.given
import com.example.ziolaminardemo.domain.*
import com.example.ziolaminardemo.http.endpoints.PersonEndpoint

import dev.cheleb.ziolaminartapir.*

object ProfilePage {

  val userBus = new EventBus[User]

  def apply() =
    div(
      child <-- session(div(h1("Please log in to view your profile")))(_ =>
        div(
          onMountCallback { _ =>
            PersonEndpoint.profile(()).emitTo(userBus)
          },
          h1("Profile Page"),
          child <-- userBus.events.map { user =>
            div(
              h2("User"),
              div("Name: ", user.name),
              div("Email: ", user.email),
              div("Age: ", user.age.toString)
            )
          }
        )
      )
    )

}
