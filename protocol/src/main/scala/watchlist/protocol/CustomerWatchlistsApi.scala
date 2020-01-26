package watchlist.protocol

import akka.actor.typed.{ActorRef, Scheduler}
import akka.actor.typed.scaladsl.AskPattern._
import watchlist.protocol.Customer.{Ack, AddItem, DeleteItem, ListItems}

import scala.concurrent.Future
class CustomerWatchlistsApi( watchListConfig: WatchListConfig,
                             actorRef: ActorRef[Customer.CustomerProtocol])(implicit
                              scheduler: Scheduler) {

  def add(customerId: CustomerId, item: Item): Future[Ack] = {
    implicit val timeout = watchListConfig.writeTimeout
    actorRef ? (actorRef => AddItem(item, customerId, actorRef))
  }

  def delete(customerId: CustomerId, item: Item): Future[Ack] = {
    implicit val timeout = watchListConfig.queryTimeout
    actorRef ? (actorRef => DeleteItem(item, customerId, actorRef))
  }


  def list(id: CustomerId): Future[List[Item]] = {
    implicit val timeout = watchListConfig.queryTimeout
    actorRef ?[List[Item]] (actorRef => ListItems(id, actorRef))
  }

}
