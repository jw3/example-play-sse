import play.twirl.sbt.Import.TwirlKeys
import sbt.Keys._

lazy val webapp = (project in file("."))
                  .settings(commonSettings)
                  .settings(webappSettings)
                  .settings(name := "play-sse-webapp")
                  .dependsOn(events)
                  .enablePlugins(PlayScala)

lazy val events = (project in file("events"))
                  .settings(commonSettings)
                  .settings(name := "play-sse-events")

lazy val commonSettings = {
    Seq(
        organization := "com.github.jw3",
        version := "0.1",
        scalaVersion := "2.11.7",
        libraryDependencies ++= all
    )
}

lazy val webappSettings = {
    Seq(
        routesGenerator := InjectedRoutesGenerator,
        sourceDirectories in TwirlKeys.compileTemplates := (unmanagedSourceDirectories in Compile).value,
        libraryDependencies += ws
    )
}

lazy val all = {
    val akkaVersion = "2.4.2-RC1"
    val akkaStreamVersion = "2.0.2"

    Seq(
        "com.typesafe" % "config" % "1.3.0",
        "org.scalatest" %% "scalatest" % "2.2.5" % Test,

        "de.heikoseeberger" %% "akka-sse" % "1.6.1",
        "com.typesafe.akka" %% "akka-actor" % akkaVersion,
        "com.typesafe.akka" %% "akka-slf4j" % akkaVersion % Runtime,
        "com.typesafe.akka" %% "akka-stream-experimental" % akkaStreamVersion,

        "org.scalatest" %% "scalatest" % "2.2.5" % Test,
        "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
        "com.typesafe.akka" %% "akka-stream-testkit-experimental" % akkaStreamVersion % Test
    )
}
