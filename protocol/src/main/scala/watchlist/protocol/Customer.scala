package watchlist.protocol

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}


case class Item(id: String)
case class CustomerId(value: String)

case class Customer(watchList: Set[Item]) {
  def add(item: Item): Customer =
    copy(watchList = watchList + item)

  def delete(item: Item): Customer =
    copy(watchList = watchList - item)
}

object Customer {
  private[protocol] def behaviour: Behavior[CustomerProtocol] = Behaviors.receiveMessage {
    case DeleteItem(_, _, replyTo) =>
      replyTo ! Ack
      Behaviors.same
    case ListItems(_, replyTo) =>
      replyTo ! List.empty
      Behaviors.same
    case AddItem(item, _, replyTo) =>
      replyTo ! Ack
      watchListBehaviour(Customer(Set(item)))

  }

  def watchListBehaviour(customer: Customer): Behavior[CustomerProtocol] = Behaviors.receiveMessage {
    case DeleteItem(item, _, replyTo) =>
      replyTo ! Ack
      watchListBehaviour(customer.delete(item))
    case AddItem(item, _, replyTo) =>
      replyTo ! Ack
      watchListBehaviour(customer.add(item))
    case ListItems(_, replyTo) =>
      replyTo ! customer.watchList.toList
      Behaviors.same
  }


  sealed trait CustomerProtocol {
    def customerId: CustomerId
  }
  sealed trait Query extends CustomerProtocol
  sealed trait Command extends CustomerProtocol
  case class AddItem(item: Item, customerId: CustomerId, replyTo: ActorRef[Ack]) extends Command
  case class DeleteItem(item: Item, customerId: CustomerId, replyTo: ActorRef[Ack]) extends Command
  case class ListItems(customerId: CustomerId, replyTo: ActorRef[List[Item]]) extends Query

  sealed trait Ack
  case object Ack extends Ack

}