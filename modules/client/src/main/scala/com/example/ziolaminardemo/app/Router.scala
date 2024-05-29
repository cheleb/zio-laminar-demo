package com.example.ziolaminardemo.app

import com.raquo.laminar.api.L.*
import frontroute.*
import org.scalajs.dom

import com.example.ziolaminardemo.app.demos.*

object Router:
  private val externalUrlBus = EventBus[String]()
  val writer                 = externalUrlBus.writer
  def apply() =
    mainTag(
      linkHandler,
      routes(
        div(
          cls := "container-fluid",
          // potentially children
          (pathEnd | path("public" / "index.html")) {
            DemosPage()
          },
          path("demos" / "scalablytyped") {
            scalablytyped.ScalablytypedDemoPage()
          },
          path("demos" / "scalariform") {
            scalariform.ScalariformDemoPage()
          }

          // path("login") {
          //   LoginPage()
          // },
          // path("signup") {
          //   SignUpPage()
          // },
          // path("change-password") {
          //   ChangePasswordPage()
          // },
          // path("forgot-password") {
          //   ForgotPasswordPage()
          // },
          // path("recover-password") {
          //   RecoverPasswordPage()
          // },
          // path("logout") {
          //   LogoutPage()
          // },
          // path("profile") {
          //   ProfilePage()
          // },
          // path("post") {
          //   CreateCompanyPage()
          // },
          // path("company" / long) {
          //   companyId =>
          //     CompanyPage(companyId)
          // },

          ,
          noneMatched {
            div("404 Not Found")
          }
        )
      )
    )
  def linkHandler =
    onMountCallback(ctx => externalUrlBus.events.foreach(url => dom.window.location.href = url)(ctx.owner))
