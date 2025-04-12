import com.typesafe.sbt.packager.archetypes.JavaAppPackaging
import com.typesafe.sbt.packager.archetypes.scripts.AshScriptPlugin
import com.typesafe.sbt.packager.docker.DockerPlugin
import com.typesafe.sbt.SbtNativePackager.autoImport._
import com.typesafe.sbt.web._
import com.typesafe.sbt.web.Import._

import java.nio.charset.StandardCharsets
import java.nio.file.Files

import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import org.scalablytyped.converter.plugin._
import org.scalablytyped.converter.plugin.STKeys._
import org.scalajs.sbtplugin._

import play.twirl.sbt.SbtTwirl

import sbt._
import sbt.Keys._

import scalajsbundler.sbtplugin._
import scalajsbundler.sbtplugin.WebScalaJSBundlerPlugin
import scalajsbundler.sbtplugin.ScalaJSBundlerPlugin.autoImport._

import webscalajs.WebScalaJS.autoImport._

import scala.collection.JavaConverters._

import scala.sys.process.{Process => ScalaProcess}

object DeploymentSettings {
//
// Define the build mode:
// - CommonJs: production mode, aka with BFF and webjar deployment
//         optimized, CommonJSModule
//         webjar packaging
// - ESModule: demo mode (default)
//         optimized, CommonJSModule
//         static files
// - dev:  development mode
//         no optimization, ESModule
//         static files, hot reload with vite.
//
// Default is "demo" mode, because the vite build does not take parameters.
//   (see vite.config.js)
  val mode = sys.env.get("MOD").getOrElse("ESModule")

  val overrideDockerRegistry = sys.env.get("LOCAL_DOCKER_REGISTRY").isDefined

  val publicFolder = "public"

//
// On dev mode, server will only serve API and static files.
//

  def staticGenerationSettings(generator: Project, client: Project) = mode match {
    case "CommonJs" =>
      Seq(
        Assets / resourceGenerators += Def
          .taskDyn[Seq[File]] {
            val rootFolder = (Assets / resourceManaged).value / publicFolder
            rootFolder.mkdirs()
            (generator / Compile / runMain).toTask {
              Seq(
                "samples.BuildIndex",
                "--title",
                s""""${name.value} v2 ${version.value}"""",
                "--version",
                version.value,
                "--resource-managed",
                rootFolder
              ).mkString(" ", " ", "")
            }
              .map(_ => (rootFolder ** "*.html").get)
          }
          .taskValue
      )
    case "ESModule" =>
      val taskOutputDir = settingKey[File]("Resource directory for task output")

      Seq(
        // ADD THIS to preserve directory structure.
        // If baseDirectory is not set, the relative path cannot be
        // calculated correctly.
        // This is needed for the webjar packaging.
        taskOutputDir := (Assets / resourceManaged).value / publicFolder,
        Assets / resourceDirectories += taskOutputDir.value,
        // Prefix the resource path with the public folder
        Assets / WebKeys.packagePrefix := s"$publicFolder/",
        // Add webjar resources to the classpath.
        Runtime / managedClasspath += (Assets / packageBin).value,
        (Assets / resourceGenerators) += Def.task {
          val rootFolder = taskOutputDir.value
          rootFolder.mkdirs()

          runCommand(
            (client / baseDirectory).value,
            streams.value.log,
            s"npm run build -- --emptyOutDir --outDir ${rootFolder.getAbsolutePath}"
          )
          IO.copyDirectory(
            (client / baseDirectory).value / "img",
            rootFolder / "img",
            overwrite = true,
            preserveLastModified = true
          )
          IO.copyDirectory(
            (client / baseDirectory).value / "css",
            rootFolder / "css",
            overwrite = true,
            preserveLastModified = true
          )
          (rootFolder ** "*.*").get

        }.taskValue
      )
    case _ =>
      Seq()
  }

  def runCommand[R](cwd: File, log: Logger, command: String): Unit = {
    val exitCode = ScalaProcess(command, cwd).!
    if (exitCode == 0) {
      log.debug(s"Command succeeded: ${command.mkString(" ")}")
    } else {
      throw new IllegalStateException(s"Command failed with exit code $exitCode")
    }
  }

  def nexusNpmSettings =
    sys.env
      .get("NEXUS")
      .map(url =>
        npmExtraArgs ++= Seq(
          s"--registry=$url/repository/npm-public/"
        )
      )
      .toSeq

  def symlink(link: File, target: File): Unit = {
    if (!(Files.exists(link.getParentFile.toPath)))
      Files.createDirectories(link.getParentFile.toPath)
    if (!(Files.exists(link.toPath) || Files.isSymbolicLink(link.toPath)))
      if (Files.exists(target.toPath))
        Files.createSymbolicLink(link.toPath, link.toPath.getParent.relativize(target.toPath))
  }
  def insureBuildEnvFile(baseDirectory: File, scalaVersion: String) = {

    val outputFile = baseDirectory / "scripts" / "target" / "build-env.sh"

    val mainJSFile = "modules/client/target/scala-$SCALA_VERSION/client-fastopt/main.js"
    lazy val buildFileContent = s"""
                                   |#!/bin/usr/env bash
                                   |
                                   |# This file is generated by build.sbt
                                   |#- On ${java.time.Instant.now}
                                   |# Do not edit it manually
                                   |
                                   |SCALA_VERSION="$scalaVersion"
                                   |MAIN_JS_FILE=$mainJSFile
                                   |""".stripMargin.split("\n").toList
    def writeBuildEnvFile(): Unit =
      IO.writeLines(
        outputFile,
        buildFileContent,
        StandardCharsets.UTF_8
      )

    if (
      outputFile.exists() && buildFileContent
        .filterNot(_.startsWith("#-")) == IO.readLines(outputFile, StandardCharsets.UTF_8).filterNot(_.startsWith("#-"))
    ) {
      println("build-env.sh file is up to date")
    } else {
      writeBuildEnvFile()
    }

  }

  lazy val dockerSettings = {
    import DockerPlugin.autoImport._
    import DockerPlugin.globalSettings._
    import sbt.Keys._
    Seq(
      Docker / maintainer     := "Joh doe",
      Docker / dockerUsername := Some("johndoe"),
      Docker / packageName    := "zio-laminar-demo",
      dockerBaseImage         := "azul/zulu-openjdk-alpine:24-latest",
      dockerRepository        := Some("registry.orb.local"),
      dockerUpdateLatest      := true,
      dockerExposedPorts      := Seq(8000)
    ) ++ (overrideDockerRegistry match {
      case true =>
        Seq(
          Docker / dockerRepository := Some("registry.orb.local"),
          Docker / dockerUsername   := Some("zio-laminar-demo")
        )
      case false =>
        Seq()
    })
  }

}
