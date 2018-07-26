name := "petl"

version := "0.1"

scalaVersion := "2.11.12"

lazy val commonProjectSettings = Seq(
)

lazy val commonScalaSettings = Seq(
  scalaVersion := "2.11.12",
  scalacOptions += "-Ypartial-unification"
)

lazy val catsVersion = "1.2.0"

lazy val circeVersion = "0.9.3"

lazy val commmonDependencies = Seq(
  libraryDependencies += "org.typelevel" %% "cats-macros" % catsVersion,
  libraryDependencies += "org.typelevel" %% "cats-kernel" % catsVersion,
  libraryDependencies += "org.typelevel" %% "cats-core" % catsVersion,
  libraryDependencies += "org.typelevel" %% "cats-free" % catsVersion
)

lazy val testDependencies = Seq(
  libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test"
)

lazy val assembly = project.in(file("assembly"))
  .settings(moduleName := "assembly")
  .settings(commonScalaSettings)
  .dependsOn(petlCli, petlApi, petlCore, hivePetl, petlTransformers)

lazy val petlCli = project.in(file("petl-cli"))
  .settings(moduleName := "petl-cli")
  .settings(commonScalaSettings)
  .dependsOn(petlApi)

lazy val petlCore = project.in(file("petl-core"))
  .settings(moduleName := "petl-core")
  .settings(commonScalaSettings)
  .dependsOn(petlApi)

lazy val petlApi = project.in(file("petl-api"))
  .settings(moduleName := "petl-api")
  .settings(commonScalaSettings)
  .settings(commmonDependencies)
  .settings(libraryDependencies ++= Seq(
    "org.yaml" % "snakeyaml" % "1.21",
    "io.circe" %% "circe-core" % circeVersion,
    "io.circe" %% "circe-literal" % circeVersion,
    "io.circe" %% "circe-generic" % circeVersion,
    "io.circe" %% "circe-generic-extras_sjs0.6" % circeVersion,
    "io.circe" %% "circe-yaml" % "0.6.1"
  ))
  .settings(testDependencies)


lazy val hivePetl = project.in(file("petl-hive"))
  .settings(moduleName := "petl-hive")
  .settings(commonScalaSettings)
  .dependsOn(petlApi)

lazy val petlTransformers = project.in(file("petl-transformers"))
  .settings(moduleName := "petl-transformers")
  .settings(commonScalaSettings)
  .dependsOn(petlApi)
