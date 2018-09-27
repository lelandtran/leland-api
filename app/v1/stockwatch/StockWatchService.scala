package v1.stockwatch

import com.redis.{RedisClient, RedisClientPool}
import javax.inject.Inject
import play.api.Configuration
import play.api.libs.ws.WSClient
import play.api.mvc.Action
import play.api.mvc.Results
import play.libs.ws.WSResponse

import scala.concurrent.{ExecutionContext, Future}

class StockWatchService @Inject()(ws: WSClient, configuration: Configuration)(implicit ec: ExecutionContext){

  val url = "https://www.alphavantage.co"
  val queryUrl = url+"/query"
  val apiKey = configuration.underlying.getString("auth.alpha_vantage.api_key")
//  val redisClients = new RedisClientPool("localhost", 6379)
  val redisClient = new RedisClient("localhost", 6379)

  def quote(symbol: String) = {
    // query redis
    val cachedStockPrice = redisClient.get("stocks:"+symbol+":price");
    if (!cachedStockPrice.isEmpty) {
      Future {
        new Stock("cached - "+symbol, cachedStockPrice.getOrElse("error"))
      }
    } else {
      // call
      ws.url(queryUrl)
        .addQueryStringParameters(("function", "GLOBAL_QUOTE"))
        .addQueryStringParameters(("symbol", symbol))
        .addQueryStringParameters(("apikey", apiKey))
        .get()
        .map(resp => {
          val price = (resp.json \ "Global Quote" \ "05. price").as[String];
          redisClient.set("stocks:"+symbol+":price", price)
          new Stock(symbol, price)
        })
    }
  }
}

class Stock (val symbol: String, val price: String) {
}