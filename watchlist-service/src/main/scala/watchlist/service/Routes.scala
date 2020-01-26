package watchlist.service

import akka.http.scaladsl.server.Directives._
import sttp.tapir.openapi.OpenAPI
import sttp.tapir.docs.openapi._
import sttp.tapir.openapi.circe.yaml._
import sttp.tapir.server.akkahttp._
import sttp.tapir.swagger.akkahttp.SwaggerAkka
import watchlist.endpoints.Watchlist
import watchlist.model.ContentId
import watchlist.protocol.{CustomerId, CustomerWatchlistsApi, Item}

import scala.concurrent.ExecutionContext

class Routes(
    api: CustomerWatchlistsApi)(implicit executionContext: ExecutionContext) {

    def allRoutes = add ~ delete ~ list ~ swaggerAkka

    private val add = Watchlist.add.toRoute {
        cmd =>
            api.add(CustomerId(cmd.customer.value), Item(cmd.content.value))
              .map(Right(_))
    }

    private val delete = Watchlist.delete.toRoute {
        cmd =>
            api.delete(CustomerId(cmd.customer.value), Item(cmd.content.value))
              .map(Right(_))
    }

    private val list = Watchlist.list.toRoute {
        query =>
            api.list(CustomerId(query.value)).map(
                _.map(_.id).map(ContentId)
            ).map(Right(_))
    }

    private val docs: OpenAPI = List(Watchlist.add, Watchlist.delete, Watchlist.list)
      .toOpenAPI("Watchlist API", "1.0")

    private val swaggerAkka =
        new SwaggerAkka(docs.toYaml).routes






}
