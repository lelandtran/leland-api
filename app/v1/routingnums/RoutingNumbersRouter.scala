package v1.routingnums

import javax.inject.Inject
import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._

class RoutingNumbersRouter @Inject()(controller: RoutingNumbersController) extends SimpleRouter {
  override def routes: Routes = {
    case GET(p"/$routingNumber") =>
      controller.show(routingNumber)
  }

  def get(routingNumber: String) = {
    controller.show(routingNumber)
  }
}
