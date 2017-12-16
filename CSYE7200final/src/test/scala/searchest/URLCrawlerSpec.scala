package searchest

import java.net.URL

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import org.scalatest.{BeforeAndAfterAll, FlatSpec}

class URLCrawlerSpec extends TestKit(ActorSystem("Example")) with ImplicitSender with FlatSpec with BeforeAndAfterAll {
  behavior of "URLCrawler"
  def test = TestActorRef(new URLCrawler(null,null))
  it should "add new url to list" in {
    val actor = test.underlyingActor
    actor.receive(ScrapMessage(new URL("http://www.example.com")))
    expectResult(actor.toProcess)(List(new URL("http://www.example.com")))
  }

  override protected def afterAll() { system.terminate() }
}