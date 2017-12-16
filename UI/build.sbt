name := "CSEY7200FINAL"

version := "FINAL"
lazy val root = (project in file(".")).enablePlugins(PlayScala)

resolvers += Resolver.sonatypeRepo("snapshots")

scalaVersion := "2.11.7"

libraryDependencies ++= {
  Seq(
    "com.typesafe.akka" %% "akka-actor"% "2.4.0",
  "org.jsoup" % "jsoup" % "1.8+",
  "commons-validator" % "commons-validator"% "1.5+",
  "tw.edu.ntu.csie" % "libsvm" % "3.17",
  "net.ruippeixotog" %% "scala-scraper" % "2.0.0"
  )
}
libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test
libraryDependencies += "com.h2database" % "h2" % "1.4.196"
