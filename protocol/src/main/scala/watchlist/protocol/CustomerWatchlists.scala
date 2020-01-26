package watchlist.protocol

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import watchlist.protocol.Customer.{AddItem, Command, ListItems}

object CustomerWatchlists {
  val behavior: Behavior[Customer.CustomerProtocol] = Behaviors.receive {
    case (ctx, l @ ListItems(customerId, replyTo)) =>
      ctx.child(customerId.value).map(_.unsafeUpcast[Customer.CustomerProtocol]) match {
        case Some(customer) =>
          customer ! l
        case None =>
          replyTo ! List.empty
      }
      Behaviors.same
    case (ctx, command: Command) =>
      val customerActor = ctx.child(command.customerId.value).map(_.unsafeUpcast[Customer.CustomerProtocol])
        .getOrElse(ctx.spawn(Customer.behaviour, command.customerId.value))
      customerActor ! command
      Behaviors.same
  }

}
