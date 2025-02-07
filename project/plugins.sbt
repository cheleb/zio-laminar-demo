// scalafmt: { maxColumn = 120, style = defaultWithAlign }

addSbtPlugin("org.scala-js" % "sbt-scalajs"        % "1.18.2")
addSbtPlugin("org.scala-js" % "sbt-jsdependencies" % "1.0.2")

addSbtPlugin("ch.epfl.scala" % "sbt-scalajs-bundler"     % "0.21.1")
addSbtPlugin("ch.epfl.scala" % "sbt-web-scalajs-bundler" % "0.21.1")

addSbtPlugin("org.scalameta"  % "sbt-scalafmt"        % "2.5.4")
addSbtPlugin("com.github.sbt" % "sbt-ci-release"      % "1.5.12")
addSbtPlugin("com.eed3si9n"   % "sbt-assembly"        % "2.3.0")
addSbtPlugin("com.github.sbt" % "sbt-native-packager" % "1.10.4")
// Static file generator
addSbtPlugin("org.playframework.twirl" % "sbt-twirl" % "2.0.7")
// Cross project support, to spread project resources between js and jvm world
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "1.3.2")
addSbtPlugin("com.github.sbt"     % "sbt-dynver"               % "5.1.0")
addSbtPlugin("com.github.sbt"     % "sbt-unidoc"               % "0.5.0")
addSbtPlugin("com.github.sbt"     % "sbt-ghpages"              % "0.8.0")
// will reStart server on code modification.
addSbtPlugin("io.spray" % "sbt-revolver" % "0.10.0")
// TypeScript support
  addSbtPlugin("org.scalablytyped.converter" % "sbt-converter" % "1.0.0-beta44")
 // Giter8 support
addSbtPlugin("org.foundweekends.giter8" % "sbt-giter8-scaffold" % "0.17.0")
// Scalafix
addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.14.0")
