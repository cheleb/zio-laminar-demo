package com.example.ziolaminardemo.app.demos

import com.raquo.laminar.api.L.*
import com.example.ziolaminardemo.app.Router

object DemosPage:

  def apply(): HtmlElement =
    div(
      h1("Demos Page"),
      ul(
        demo("Scalablytyped", "/demos/scalablytyped"),
        demo("Scalariform", "/demos/scalariform"),
        a("Metrics", onClick.mapTo("/metrics") --> Router.writer)
      )
    )

  private def demo(title: String, link: String) =
    li(
      a(
        href := link,
        title
      )
    )
