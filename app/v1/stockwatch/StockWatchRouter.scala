import com.google.inject.Inject
import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._
import v1.stockwatch.StockWatchController

class StockWatchRouter @Inject()(controller : StockWatchController) extends SimpleRouter {

  override def routes: Routes = {
    case GET(p"/$symbol") =>
      controller.show(symbol)
  }

  def get(symbol: String) = {
    controller.show(symbol)
  }
}