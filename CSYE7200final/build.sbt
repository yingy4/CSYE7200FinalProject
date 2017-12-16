name := "akka-web-crawler"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= {
  val akkaV       = "2.4.0"
  Seq(
    "com.typesafe.akka" %% "akka-actor"% akkaV,
    "org.jsoup" % "jsoup" % "1.8+",
    "commons-validator" % "commons-validator"% "1.5+",
    "tw.edu.ntu.csie" % "libsvm" % "3.17",
    "net.ruippeixotog" %% "scala-scraper" % "2.0.0",
  "org.scalaz" %% "scalaz-concurrent" % "7.3.0-M18",
  "org.scalatest" % "scalatest_2.10" % "1.9.1" % "test",
  "com.typesafe.akka" %% "akka-testkit" % "2.4.0"
  )
}