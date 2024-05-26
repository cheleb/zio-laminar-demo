package com.example.ziolaminardemo.app.demos

import com.raquo.laminar.api.L.*

object DemosPage:

  def apply(): HtmlElement =
    div(
      h1("Demos Page"),
      ul(
        demo("Scalablytyped", "/demos/scalablytyped"),
        demo("Scalariform", "/demos/scalariform")
      )
    )

  private def demo(title: String, link: String) =
    li(
      a(
        href := link,
        title
      )
    )
