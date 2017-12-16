package searchest

import java.net.URL

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import org.scalatest.{BeforeAndAfterAll, FlatSpec}

class URLScraperSpec extends TestKit(ActorSystem("Example")) with ImplicitSender with FlatSpec with BeforeAndAfterAll {
  behavior of "URLScraper"
  def test = TestActorRef(new URLScraper(null,2))
  it should "Sent ScrapFinishedMessage back" in {
    val actor = test.underlyingActor
    test ! (ScrapMessage(new URL("http://www.example.com")))
    expectMsg(ScrapFinishedMessage(new URL("http://www.example.com")))
  }

  override protected def afterAll() { system.terminate() }
}