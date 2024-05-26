package samples

import com.raquo.laminar.api.L.*
import org.scalajs.dom

@main def main: Unit = 

  val myApp = 
    div(
      h1("Hello World")
    )

  val containerNode = dom.document.getElementById("app")
  render(containerNode, myApp)


