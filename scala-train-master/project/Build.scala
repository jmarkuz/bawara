import sbt._
import Keys._

object ScalaTrainBuild extends Build {
  val macroVersion = "2.0.1"
  val paradisePlugin = compilerPlugin("org.scalamacros" % "paradise" % macroVersion cross CrossVersion.full)

  val akkaVersion = "2.3.11"
  val akkaDeps = Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-kernel" % akkaVersion,
    "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
    "com.typesafe.akka" %% "akka-remote" % akkaVersion,
    "com.typesafe.akka" %% "akka-persistence-experimental" % akkaVersion,
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion
  )
  val defaultSettings = Seq(
    version := "1.0",
    scalaVersion := "2.11.6",
    //testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-h", "target/test-reports"),
    scalacOptions     ++= Seq(
      "-encoding", "UTF-8",
      "-feature",
      "-language:implicitConversions", "-language:higherKinds", "-language:postfixOps"
    ),
    libraryDependencies ++= Seq(
      "org.scala-lang.modules" %% "scala-xml" % "1.0.2",
      "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.2",
      "org.scalatest" %% "scalatest" % "2.2.4" % "test",
      "org.scalacheck" %% "scalacheck" % "1.12.2" % "test",
      "org.seleniumhq.selenium" % "selenium-java" % "2.35.0" % "test",
      "org.pegdown" % "pegdown" % "1.4.2" % "test",
      "org.mockito" % "mockito-core" % "2.0.7-beta",
      "org.scalaz" %% "scalaz-core" % "7.1.1",
      "org.hibernate.javax.persistence" % "hibernate-jpa-2.0-api" % "1.0.1.Final"
    ) ++ akkaDeps
  )

  lazy val root: Project = Project(
    "scala-train",
    file("."),
    settings = defaultSettings ++ Seq(
      run <<= run in Compile in macros
    )) aggregate(core, macros)

  lazy val core: Project = Project(
    "scala-train-core",
    file("core"),
    settings = defaultSettings
  ) dependsOn(macros)

  lazy val macros: Project = Project(
    "scala-train-macro",
    file("macro"),
    settings = defaultSettings ++ Seq(
      scalacOptions += "-language:experimental.macros",
      libraryDependencies ++= Seq(
        "org.scala-lang" % "scala-reflect" % scalaVersion.value,
        "org.scala-lang" % "scala-compiler" % scalaVersion.value % "provided"
      ),
      addCompilerPlugin(paradisePlugin),
      libraryDependencies ++= CrossVersion partialVersion scalaVersion.value collect {
        case (2, scalaMajor) if scalaMajor < 11 =>
          // if scala 2.11+ is used, quasiquotes are merged into scala-reflect
          Seq("org.scalamacros" %% "quasiquotes" % macroVersion)
      } getOrElse Nil
    )
  )
}
