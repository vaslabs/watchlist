package watchlist.protocol

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import watchlist.protocol.Customer.{Ack, AddItem, CustomerProtocol, DeleteItem, ListItems}


case class Item(id: String)
case class CustomerId(value: String)

case class Customer(watchList: Set[Item]) {
  def add(item: Item): Customer =
    copy(watchList = watchList + item)

  def delete(item: Item): Customer =
    copy(watchList = watchList - item)
}


case class Reply[A, F[_]](msg: A, replyTo: F[A])
case class Actions[F[_]](reply: Reply[_, F])

sealed trait State {
  def handle[F[_]](msg: CustomerProtocol[F]): (State, Actions[F])
}
case object EmptyState extends State {
  override def handle[F[_]](msg: CustomerProtocol[F]): (State, Actions[F]) = msg match {
    case ListItems(_, replyTo) =>
      this -> Actions(Reply(List.empty, replyTo))
    case AddItem(item, _, replyTo) =>
      StateWithCustomer(Customer(Set(item))) -> Actions(Reply(Ack, replyTo))
    case DeleteItem(_, _, replyTo) =>
      this -> Actions(Reply(Ack, replyTo))
  }
}
case class StateWithCustomer(customer: Customer) extends State {
  override def handle[F[_]](msg: CustomerProtocol[F]): (State, Actions[F]) = msg match {
    case ListItems(_, replyTo) =>
      this -> Actions(Reply(customer.watchList.toList, replyTo))
    case AddItem(item, _, replyTo) =>
      StateWithCustomer(customer.add(item)) -> Actions(Reply(Ack, replyTo))
    case DeleteItem(item, _, replyTo) =>
      StateWithCustomer(customer.delete(item)) -> Actions(Reply(Ack, replyTo))
  }
}


object Customer {

  private[protocol] def fb_behaviour(state: State): Behavior[CustomerProtocol[ActorRef]] = Behaviors.receiveMessage {
    msg =>
      val (newState, actions) = state.handle(msg)

      val reply = actions.reply

      reply.replyTo.unsafeUpcast[Any] ! reply.msg

      fb_behaviour(newState)
  }

  private[protocol] def behaviour: Behavior[CustomerProtocol[ActorRef]] = fb_behaviour(state = EmptyState)


  sealed trait CustomerProtocol[F[_]] {
    def customerId: CustomerId
  }
  sealed trait Query[F[_]] extends CustomerProtocol[F]
  sealed trait Command[F[_]] extends CustomerProtocol[F]
  case class AddItem[F[_]](item: Item, customerId: CustomerId, replyTo: F[Ack]) extends Command[F]
  case class DeleteItem[F[_]](item: Item, customerId: CustomerId, replyTo: F[Ack]) extends Command[F]
  case class ListItems[F[_]](customerId: CustomerId, replyTo: F[List[Item]]) extends Query[F]

  sealed trait Ack
  case object Ack extends Ack

}