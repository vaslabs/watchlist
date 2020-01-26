package watchlist.protocol

import akka.actor.testkit.typed.scaladsl.ActorTestKit
import akka.util.Timeout
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AsyncWordSpec

import scala.concurrent.duration._
class CustomerWatchListSpec extends AsyncWordSpec with Matchers with BeforeAndAfterAll {

  val testKit = ActorTestKit()
  implicit val scheduler = testKit.system.scheduler

  override def afterAll(): Unit = testKit.shutdownTestKit()

  val customerWatchlists = testKit.spawn(CustomerWatchlists.behavior)
  val config = WatchListConfig(Timeout(1 second), Timeout(1 second))
  val customerWatchlistsApi = new CustomerWatchlistsApi(config, customerWatchlists)

  "customer watchlists" must {
    val customerId = CustomerId("123")
    val firstCustomerItems = List("i1", "i2", "i3")

    "give empty if they have no items" in {
      customerWatchlistsApi.list(customerId).map(_ mustBe List.empty[Item])
    }

    "give added items" in {
      val addItems = firstCustomerItems.map(Item)
      addItems.foreach(item => customerWatchlistsApi.add(customerId, item))
      customerWatchlistsApi.list(customerId).map(_ mustBe addItems)
    }

    "give added items of multiple customers" in {
      val secondCustomerItems = List("ia", "ib", "ic").map(Item)
      val secondCustomerId = CustomerId("abc")
      secondCustomerItems.foreach(item => customerWatchlistsApi.add(secondCustomerId, item))
      customerWatchlistsApi.list(secondCustomerId).map(_ mustBe secondCustomerItems)
    }

  }

}
