package v1.routingnums

import java.util.concurrent.Future

import scala.concurrent.duration._
import akka.actor.ActorSystem
import com.redis.RedisClient
import javax.inject.Inject
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, BaseController, ControllerComponents, DefaultControllerComponents}

import scala.concurrent.{ExecutionContext, Promise}
import scala.concurrent.duration.FiniteDuration

class RoutingNumbersController @Inject()(cc: DefaultControllerComponents, ws: WSClient)(implicit ec: ExecutionContext) extends BaseController {
  override protected def controllerComponents: ControllerComponents = cc

  val rc = new RedisClient("localhost", 6379)

  def show(routingNumber: String) = Action.async{ implicit request =>
    ws.url("https://www.routingnumbers.info/api/data.json")
      .addQueryStringParameters(("rn", routingNumber))
      .get()
      .map(resp => {
        rc.incrby("routingNumber:"+routingNumber+":reqs", 1)
          .map(reqs => (resp, reqs))
      })
      .map(respAndReqs => Ok("Req count for " + routingNumber + ":" +respAndReqs.get._2 + "\n resp:"+respAndReqs.get._1.body))
  }
}
