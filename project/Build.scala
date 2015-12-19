import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

//    lazy val PlayApp = Project("avii", file("src/main"))

    val appName         = "avii_website"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      "org.simpleframework" % "simple-xml" % "2.7",
      "com.novocode" % "junit-interface" % "0.8" % "test->default"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = JAVA).settings(
      // Add your own project settings here
    )

}
