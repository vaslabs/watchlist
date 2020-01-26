package watchlist.endpoints.json.circe

import io.circe.{Decoder, Encoder}
import watchlist.model.{ContentId, CustomerId}

object Watchlist {

  implicit val customerIdEncoder: Encoder[CustomerId] =
    Encoder.encodeString.contramap(_.value)

  implicit val customerIdDecoder: Decoder[CustomerId] = Decoder.decodeString.map(
    CustomerId
  )

  implicit val contentIdEncoder: Encoder[ContentId] = Encoder.encodeString.contramap(_.value)
  implicit val contentIdDecoder: Decoder[ContentId] = Decoder.decodeString.map(ContentId)

}
