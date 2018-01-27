package v1.post

import com.google.inject.Inject
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent}

import scala.concurrent.ExecutionContext

class PostController @Inject()(cc: PostControllerComponents)(implicit ec: ExecutionContext)
  extends PostBaseController(cc) {

  private val logger = Logger(getClass)

  def index: Action[AnyContent] = PostAction.async { implicit request =>
    logger.trace("index: ")
    postResourceHandler.find.map{ posts =>
      Ok(Json.toJson(posts))
    }
  }

  def process: Action[AnyContent] = PostAction.async { implicit request =>
    logger.trace("process: ")
    postResourceHandler.create.map { post =>
      Ok(Json.toJson(post))
    }
  }
}
