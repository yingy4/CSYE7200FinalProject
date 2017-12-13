package searchest

import java.net.URL
import scala.language.implicitConversions

import akka.actor.{Actor, ActorSystem, ActorRef, Props, _}
import akka.util.Timeout
import akka.pattern.{ask, pipe}

import org.apache.commons.validator.routines.UrlValidator
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.Future
import scala.language.postfixOps
import scala.collection.JavaConverters._
import net.ruippeixotog.scalascraper.browser.JsoupBrowser

import java.io._

import org.jsoup.Jsoup

case class StartMessage(url: URL)
case class ScrapMessage(url: URL)
case class IndexMessage(url: URL, content: ContentMessage)
case class ContentMessage(title: String, meta: String, urls: List[URL])
case class ScrapFinishedMessage(url: URL)
case class WriterFinishedMessage(url: URL, urls: List[URL])
case class ScrapFailureMessage(url: URL, reason: Throwable)

package object searchest {
  implicit def stringtourl(s: String): URL = new URL(s)
  implicit def stringtourlWithSpec(s: (String, String)): URL = new URL(new URL(s._1), s._2)
}

class Master(system: ActorSystem) extends Actor {
  val htmlwriter = context actorOf Props(new HtmlWriter(self))

  val maxPages = 2
  val retryTimes = 2

  var numVisited = 0
  var toScrap = Set.empty[URL]
  var scrapCounts = Map.empty[URL, Int]
  var host2Actor = Map.empty[String, ActorRef]

  def receive: Receive = {
    case StartMessage(url) =>
      scrap(url)
    case ScrapFinishedMessage(url) =>
    case WriterFinishedMessage(url, urls) =>
      if (numVisited < maxPages)
        urls.toSet.filter(l => !scrapCounts.contains(l)).foreach(scrap)
      checkAndShutdown(url)
    case ScrapFailureMessage(url, reason) =>
      val retries: Int = scrapCounts(url)
      if (retries < retryTimes) {
        countVisits(url)
        host2Actor(url.getHost) ! ScrapMessage(url)
      } else
        checkAndShutdown(url)
  }

  def checkAndShutdown(url: URL): Unit = {
    toScrap -= url
    // nothing to visit
    if (toScrap.isEmpty) {
      self ! PoisonPill
      system.terminate()
    }
  }

  def scrap(url: URL) = {
    val host = url.getHost
    if (!host.isEmpty) {
      val actor = host2Actor.getOrElse(host, {
        val buff = system.actorOf(Props(new URLCrawler(self, htmlwriter)))
        host2Actor += (host -> buff)
        buff
      })

      numVisited += 1
      toScrap += url
      countVisits(url)
      actor ! ScrapMessage(url)
    }
  }

  def countVisits(url: URL): Unit = scrapCounts += (url -> (scrapCounts.getOrElse(url, 0) + 1))
}

class URLCrawler(master: ActorRef, htmlwriter: ActorRef) extends Actor {

  val process = "Process next url"

  val urlscraper = context actorOf Props(new URLScraper(htmlwriter))
  implicit val timeout = Timeout(3 seconds)
  val tick =
    context.system.scheduler.schedule(0 millis, 1000 millis, self, process)
  var toProcess = List.empty[URL]

  def receive: Receive = {
    case ScrapMessage(url) =>
      // wait some time, so we will not spam a website
      toProcess = url :: toProcess
    case `process` =>
      toProcess match {
        case Nil =>
        case url :: list =>
          toProcess = list
          (urlscraper ? ScrapMessage(url)).mapTo[ScrapFinishedMessage]
            .recoverWith { case e => Future {ScrapFailureMessage(url, e)} }
            .pipeTo(master)
      }
  }
}

class URLScraper(htmlwriter: ActorRef) extends Actor {
  val urlValidator = new UrlValidator()

  def receive: Receive = {
    case ScrapMessage(url) =>
      val content = parse(url)
      sender() ! ScrapFinishedMessage(url)
      htmlwriter ! IndexMessage(url, content)
  }

  def parse(url: URL): ContentMessage = {
    val link: String = url.toString
    val response = Jsoup.connect(link).ignoreContentType(true)
      .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.1").execute()

    val contentType: String = response.contentType
    if (contentType.startsWith("text/html")) {
      val doc = response.parse()
      val title: String = doc.getElementsByTag("title").asScala.map(e => e.text()).head
      val descriptionTag = doc.getElementsByTag("meta").asScala.filter(e => e.attr("name") == "description")
      val description = if (descriptionTag.isEmpty) "" else descriptionTag.map(e => e.attr("content")).head
      val links: List[URL] = doc.getElementsByTag("a").asScala.map(e => e.attr("href")).filter(s =>
        urlValidator.isValid(s)).map(link => new URL(link)).toList
      ContentMessage(title, description, links)
    } else {
      // e.g. if this is an image
      ContentMessage(link, contentType, List())
    }
  }
}

class HtmlWriter(master: ActorRef) extends Actor {
  var store = Map.empty[URL, ContentMessage]
  val browser = JsoupBrowser()

  def receive: Receive = {
    case IndexMessage(url, content) =>
      store += (url -> content)
      val html = browser.get(url.toString)

      val output1 = "C:\\Users\\XiaoCase\\Desktop\\CSYE7200Final\\src\\scalaplus\\data\\html"
      val filename = trim(url.toString)
      val pw = new PrintWriter(new File(s"$output1\\$filename.html"))
      pw.write(trim(html.toString))
      pw.close
      // println(s"html $html")
      master ! WriterFinishedMessage(url, content.urls)
  }

  def trim(s:String):String = {
    val filename1 = s.toString.replaceAll("/", "")
    val filename2 = filename1.replaceAll(":", "")
    val filename3 = filename2.replaceAll(">", "")
    val filename4 = filename3.replaceAll("<", "")
    val filename = filename4.filter(_!='?')
    filename
  }

  @throws[Exception](classOf[Exception])
  override def postStop(): Unit = {
    super.postStop()
   // store.foreach(println)
 //   println(store.size)
  }
}