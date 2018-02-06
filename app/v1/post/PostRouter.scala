package v1.post

import com.google.inject.Inject
import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._

class PostRouter @Inject()(controller: PostController) extends SimpleRouter{

  val prefix = "/v1/posts"

  def link(id: PostId): String = {
    import com.netaporter.uri.dsl._
    val url = prefix / id.toString
    url.toString
  }

  override def routes: Routes = {
    case GET(p"/") =>
      controller.index

    case POST(p"/") =>
//      controller.index
      controller.process

    case GET(p"/$id") =>
      controller.index
  }

  def getAll = {
    controller.index
  }

  def create = {
    controller.process
  }

  def get(id: String) = {
    controller.show(id)
  }
}
