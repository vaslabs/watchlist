package watchlist.protocol

import akka.util.Timeout

case class WatchListConfig(queryTimeout: Timeout, writeTimeout: Timeout)
