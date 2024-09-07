package com.example.ziolaminardemo.app

import com.raquo.laminar.api.L.*

object HomePage:

  def apply(): HtmlElement =
    div(
      cls := "center",
      div(
        img(src := "img/scala.svg", width := "200px")
      ),
      div(
        img(src := "img/zio.png")
      ),
      div(
        img(src := "img/tapir.svg", width := "100px"),
        img(src := "img/sml.png", width   := "200px")
      ),
      div(
        ul(
          demo("Scalablytyped", "/demos/scalablytyped"),
          demo("Scalariform", "/demos/scalariform"),
          a("Metrics", onClick.mapTo("http://localhost:8080/metrics") --> Router.writer)
        )
      )
    )

  private def demo(title: String, link: String) =
    li(
      a(
        href := link,
        title
      )
    )
