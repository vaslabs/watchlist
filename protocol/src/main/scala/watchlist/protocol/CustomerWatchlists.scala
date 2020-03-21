package watchlist.protocol

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import watchlist.protocol.Customer.{AddItem, Command, ListItems}

object CustomerWatchlists {
  val behavior: Behavior[Customer.CustomerProtocol[ActorRef]] = Behaviors.receive {
    case (ctx, l @ ListItems(customerId, replyTo)) =>
      ctx.child(customerId.value).map(_.unsafeUpcast[Customer.CustomerProtocol[ActorRef]]) match {
        case Some(customer) =>
          customer ! l
        case None =>
          replyTo ! List.empty
      }
      Behaviors.same
    case (ctx, command: Command[ActorRef]) =>
      val customerActor = ctx.child(command.customerId.value).map(_.unsafeUpcast[Customer.CustomerProtocol[ActorRef]])
        .getOrElse(ctx.spawn(Customer.behaviour, command.customerId.value))
      customerActor ! command
      Behaviors.same
  }

}
