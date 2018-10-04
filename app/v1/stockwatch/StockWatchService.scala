package v1.stockwatch

import com.redis.RedisClient
import javax.inject.Inject
import play.api.Configuration
import play.api.libs.ws.WSClient

import scala.concurrent.{ExecutionContext, Future}

class StockWatchService @Inject()(ws: WSClient, configuration: Configuration)(implicit ec: ExecutionContext){

  val url = "https://www.alphavantage.co"
  val queryUrl = url+"/query"
  val apiKey = configuration.underlying.getString("auth.alpha_vantage.api_key")
//  val redisClients = new RedisClientPool("localhost", 6379)
  val redisClient = new RedisClient("localhost", 6379)
  val TTL = 10;

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
          val priceKey = "stocks:"+symbol+":price";
          redisClient.set(priceKey, price)
          redisClient.expire(priceKey, TTL)
          new Stock(symbol, price)
        })
    }
  }

  def add(symbol: String) = {
    Future {
      redisClient.sadd("stocks", symbol);
      redisClient.smembers("stocks").getOrElse(Set.empty[Option[String]]);
    }
  }

  def list(): Future[Set[Option[String]]] = {
    Future {
      redisClient.smembers("stocks").getOrElse(Set.empty[Option[String]])
    }
  }
}

class Stock (val symbol: String, val price: String) {
}