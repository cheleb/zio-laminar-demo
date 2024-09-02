package com.example.ziolaminardemo.app

import be.doeraene.webcomponents.ui5.*
import be.doeraene.webcomponents.ui5.configkeys.*
import com.raquo.laminar.api.L.*
import org.scalajs.dom.HTMLElement

import dev.cheleb.scalamigen.{*, given}
import dev.cheleb.ziolaminartapir.ZJS.*

import com.example.ziolaminardemo.app.login.LoginPasswordUI
import com.example.ziolaminardemo.http.endpoints.PersonEndpoint
import dev.cheleb.ziolaminartapir.Session
import com.example.ziolaminardemo.domain.UserToken

import dev.cheleb.ziolaminartapir.SessionLive

given session: Session[UserToken] = SessionLive[UserToken]

object Header:
  private val openPopoverBus = new EventBus[HTMLElement]
  private val profileId      = "profileId"

  val credentials = Var(LoginPasswordUI("", ""))

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
          Button(
            "Login",
            onClick --> { _ =>
              loginHandler(session)
            }
          ),
          UList(
            _.separators := ListSeparator.None,
            _.item(_.icon := IconName.settings, "Settings"),
            _.item(_.icon := IconName.`sys-help`, "Help"),
            _.item(_.icon := IconName.log, "Sign out")
          )
        )
      )
    )

  def loginHandler(session: Session[UserToken]): Unit =
    PersonEndpoint
      .login(credentials.now().http)
      .map(token => session.setUserState(token))
      .runJs
