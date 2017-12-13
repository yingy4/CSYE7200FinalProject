package searchest

import java.net.URL

import akka.actor.{ActorSystem, PoisonPill, Props}

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps
import java.io._
import java.nio.file.{Files, Path, Paths}
import org.apache.commons.io.FileUtils

object XiaoCase extends App {
  val actsystem = ActorSystem()
  val master = actsystem.actorOf(Props(new Master(actsystem)))

  val output1 = "C:\\Users\\XiaoCase\\Desktop\\CSYE7200Final\\src\\scalaplus\\data\\html"
  //delete the old output folder and create a new one
  FileUtils.deleteQuietly(new File(output1))
  new File(output1).mkdirs();
  println("What do you want to know : ")
  val input = scala.io.StdIn.readLine()
  if (input.length == 0) {
    println("Please input something")
  }

  val url = new URL("https://www.google.com/search?q="+input)
  master ! StartMessage(url)

  Await.result(actsystem.whenTerminated, 3 minutes)

  master ! PoisonPill
  actsystem.terminate



    println("procrssing")
    val p2 = Runtime.getRuntime().exec("D:\\createworld\\python\\python src\\scalaplus\\topic_modelr.py")
    p2.waitFor()
    println("done")
}
