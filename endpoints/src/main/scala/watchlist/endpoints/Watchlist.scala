package watchlist.endpoints

import json.circe.Watchlist._
import sttp.model.StatusCode
import sttp.tapir.Validator.Custom
import sttp.tapir._
import sttp.tapir.json.circe._
import watchlist.model.{Add, ContentId, CustomerId, Delete}

object Watchlist {
  import io.circe.generic.auto._

  implicit val customerIdSchema: Schema[CustomerId] = Schema(SchemaType.SString)
  implicit val contentIdSchema: Schema[ContentId] = Schema(SchemaType.SString)

  implicit val customerIdCodec =
    Codec.stringPlainCodecUtf8.map(CustomerId)(_.value)
      .validate(Custom (
        _.value.size == 3,
        "Malformed customer id"
      ))

  final val CustomerIdHeader = "X-WATCHLIST-ID"

  val add =
    endpoint
      .put
      .in("watchlist")
      .in(jsonBody[Add])
      .out(statusCode(StatusCode.Accepted))

  val delete =
    endpoint.delete
    .in("watchlist")
    .in(jsonBody[Delete])
    .out(statusCode(StatusCode.Accepted))

  val list =
    endpoint.get
    .in("watchlist")
    .in(header[CustomerId](CustomerIdHeader))
    .out(jsonBody[List[ContentId]])

}
