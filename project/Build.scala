import sbt._
import sbt.Keys._

object SlickTestingBuild extends Build {

  lazy val slickTesting = Project(
    id = "slick-testing",
    base = file("."),
    settings = Seq(
      name := "Slick Testing",
      organization := "com.daverstevens",
      version := "0.1-SNAPSHOT",
      scalaVersion := "2.11.2"
      // add other settings here
    )
  )
}
