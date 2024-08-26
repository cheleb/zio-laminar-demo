package com.example.ziolaminardemo.app

import be.doeraene.webcomponents.ui5.*
import be.doeraene.webcomponents.ui5.configkeys.*
import com.raquo.laminar.api.L.*
import org.scalajs.dom.HTMLElement

import com.example.ziolaminardemo.http.LoginPassword
import dev.cheleb.scalamigen.{*, given}

object Header:
  private val openPopoverBus = new EventBus[HTMLElement]
  private val profileId      = "profileId"

  val credentials = Var(LoginPassword("", ""))

  def apply(): HtmlElement =
    div(
      ShellBar(
        _.primaryTitle       := "ZIO Laminar Demo",
        _.secondaryTitle     := "Secondary title",
        _.notificationsCount := "99+",
        _.showNotifications  := true,
//        _.showProductSwitch  := true,
        _.showCoPilot   := true,
        _.slots.profile := Avatar(idAttr := profileId, img(src := "questionmark.jpg")),
        _.events.onProfileClick
          .map(_.detail.targetRef) --> openPopoverBus.writer
      ),
      Popover(
        _.openerId := profileId,
        _.open <-- openPopoverBus.events.mapTo(true),
        // _.placement := PopoverPlacementType.Bottom,
        div(Title(padding := "0.25rem 1rem 0rem 1rem", "Login")),
        div(
          credentials.asForm,
          UList(
            _.separators := ListSeparator.None,
            _.item(_.icon := IconName.`sys-find`, "App Finder"),
            _.item(_.icon := IconName.settings, "Settings"),
            _.item(_.icon := IconName.edit, "Edit Home Page"),
            _.item(_.icon := IconName.`sys-help`, "Help"),
            _.item(_.icon := IconName.log, "Sign out")
          )
        )
      )
    )
