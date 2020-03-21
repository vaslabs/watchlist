package watchlist.protocol

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import watchlist.protocol.Customer.{Ack, AddItem, ListItems}

object ops {
  type Id[A] = A

  final implicit class SenderOps(actions: Actions[Id]) extends Matchers {

    def expectMessage[A] = {
      actions.reply.msg mustBe actions.reply.replyTo
    }
  }

}
class CustomerStateSpec extends AnyWordSpec with Matchers{


  import ops._

  def ActorRef[A](a: A): Id[A] = a

  "customer watchlist" must {
    val state = EmptyState
    "initially be empty" in {

      val (_, actions) = state.handle(ListItems[Id](CustomerId("123"), ActorRef(List.empty)))

      actions.expectMessage
    }

    "give items that were added" in {
      val sendingItem = Item("a")

      val (newState, actions) = EmptyState.handle(AddItem[Id](sendingItem, CustomerId("123"), Ack))

      actions.expectMessage

      newState.handle(ListItems[Id](CustomerId("123"), ActorRef(List(sendingItem))))._2.expectMessage

    }
  }

}
