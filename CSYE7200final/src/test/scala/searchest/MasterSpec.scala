package searchest

import java.net.URL

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import org.scalatest.{BeforeAndAfterAll, FlatSpec}

class MasterSpec extends TestKit(ActorSystem("Example")) with ImplicitSender with FlatSpec with BeforeAndAfterAll {
  behavior of "URLScraper"
  def test = TestActorRef(new Master(system))
  it should "sent job finished message" in {
    val actor = test.underlyingActor
    test ! (ScrapFinishedMessage(new URL("http://www.example.com")))
    expectMsg(GoodJobMessage("well done"))
  }

  override protected def afterAll() { system.terminate() }
}