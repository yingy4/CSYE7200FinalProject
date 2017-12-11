name := "akka-web-crawler"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= {
  val akkaV       = "2.4.0"
  Seq(
    "com.typesafe.akka" %% "akka-actor"% akkaV,
    "org.jsoup" % "jsoup" % "1.8+",
    "commons-validator" % "commons-validator"% "1.5+",
    "org.apache.spark" %% "spark-mllib" % "2.2.0" % "provided",
    "org.apache.spark" %% "spark-sql" % "2.2.0",
    "tw.edu.ntu.csie" % "libsvm" % "3.17",
    "net.ruippeixotog" %% "scala-scraper" % "2.0.0"
  )
}