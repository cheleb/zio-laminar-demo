package com.example.ziolaminardemo.app.profile

import com.raquo.laminar.api.L.*

import com.example.ziolaminardemo.app.given
import com.example.ziolaminardemo.domain.*
import com.example.ziolaminardemo.http.endpoints.PersonEndpoint

import dev.cheleb.ziotapir.laminar.*

object ProfilePage:

  val userBus = new EventBus[(User, Option[Pet])]

  def apply() = div(
    child <-- session:
      // If the user is not logged in, show a message
      div(h1("Please log in to view your profile"))
      // If the user is logged in, show the profile page
    (_ =>      div(
        onMountCallback { _ =>
          PersonEndpoint.profile(false).emitTo(userBus)
        },
        div(
          h1("Profile Page"),
          child <-- userBus.events.map { case (user, maybePet) =>
            div(
              cls := "srf-form",
              h2("User"),
              div("Name: ", user.name),
              div("Email: ", user.email),
              div("Age: ", user.age.toString),
              user.petType.map(pt => s"Has a $pt").getOrElse("No pet"),
              input(
                tpe     := "checkbox",
                checked := maybePet.isDefined,
                onInput.mapToChecked --> { withPet =>
                  PersonEndpoint.profile(withPet).emitTo(userBus)
                }
              ),
              maybePet.map { pet =>
                div(
                  h2("Pet"),
                  div("Name: ", pet.name),
                  div(
                    "Type: ",
                    pet match {
                      case _: Cat => "Cat"
                      case _: Dog => "Dog"
                    }
                  )
                )
              }.getOrElse(div())
            )
          }
        )
      )
    )
  )
