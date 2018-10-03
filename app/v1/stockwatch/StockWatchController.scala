package v1.stockwatch

import javax.inject.Inject
import play.api.libs.json.Json
import play.api.mvc.{Action, BaseController, ControllerComponents, DefaultControllerComponents}

import scala.concurrent.ExecutionContext

class StockWatchController @Inject()(stockWatchService: StockWatchService, cc: DefaultControllerComponents)(implicit ec: ExecutionContext) extends BaseController {
  override protected def controllerComponents: ControllerComponents = cc

  def show(symbol: String) = Action.async { implicit request =>
    stockWatchService.quote(symbol)
      .map(res => Ok(Json.toJson(res.symbol -> res.price)))
  }

  def add(symbol: String) = Action.async { implicit request =>
    stockWatchService.add(symbol)
      .map(res => Ok(Json.toJson(res)))
  }

  def list() = Action.async { implicit request =>
    stockWatchService.list()
      .map(res => Ok(Json.toJson(res)))
  }
}
