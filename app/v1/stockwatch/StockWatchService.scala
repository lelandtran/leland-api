package v1.stockwatch

import com.redis.RedisClient
import javax.inject.Inject
import play.api.Configuration
import play.api.libs.ws.WSClient
import play.api.mvc.Action
import play.api.mvc.Results

import scala.concurrent.ExecutionContext

class StockWatchService @Inject()(ws: WSClient, configuration: Configuration){

  val url = "https://www.alphavantage.co"
  val queryUrl = url+"/query"
  val apiKey = configuration.underlying.getString("auth.alpha_vantage.api_key")
  val rc = new RedisClient("localhost", 6379)

  def quote(symbol: String) = {
    ws.url(queryUrl)
      .addQueryStringParameters(("function", "GLOBAL_QUOTE"))
      .addQueryStringParameters(("symbol", symbol))
      .addQueryStringParameters(("apikey", apiKey))
      .get()
  }
}
