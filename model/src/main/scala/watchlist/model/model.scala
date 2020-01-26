package watchlist.model

case class ContentId(value: String)
case class CustomerId(value: String)

sealed trait Command {
  def content: ContentId
  def customer: CustomerId
}

case class Add(content: ContentId, customer: CustomerId) extends Command
case class Delete(content: ContentId, customer: CustomerId) extends Command


sealed trait Query {
  def customer: CustomerId
}

case class ListItems(customer: CustomerId) extends Query