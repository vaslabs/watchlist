package watchlist.service

import akka.actor.typed.{ActorSystem, Behavior}
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.scaladsl.adapter._
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import watchlist.protocol.{CustomerWatchlists, CustomerWatchlistsApi, WatchListConfig}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

object Bootstrap extends App {


  val config = WatchListConfig(Timeout(1 second), Timeout(1 second))


  sealed trait GuardianProtocol
  val guardianBehaviour: Behavior[GuardianProtocol] =
    Behaviors.setup {
      ctx =>
        implicit val scheduler =
          ctx.system.scheduler
        val customerWatchlists = ctx.spawn(CustomerWatchlists.behavior, "CustomerWatchlists")

        val api = new CustomerWatchlistsApi(config, customerWatchlists)

        val routes = new Routes(api)(ExecutionContext.global)

        startServer(ctx, routes.allRoutes)
        Behaviors.ignore
    }


  private def startServer(actorContext: ActorContext[_], routes: Route) = {
    implicit val actorSystem = actorContext.system.toClassic
    Http().bindAndHandle(routes, "0.0.0.0", 8080)
  }


  val actorSystem = ActorSystem(guardianBehaviour, "watchlist")

  sys.addShutdownHook {
    actorSystem.terminate()
  }
}
