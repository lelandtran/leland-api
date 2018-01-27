package v1.post

import com.google.inject.Inject
import net.logstash.logback.marker.LogstashMarker
import play.api.{Logger, MarkerContext}
import play.api.http.{FileMimeTypes, HttpVerbs}
import play.api.i18n.{Langs, MessagesApi}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

trait PostRequestHeader extends MessagesRequestHeader with PreferredMessagesProvider
class PostRequest[A](request: Request[A], val messagesApi: MessagesApi) extends WrappedRequest(request) with PostRequestHeader

trait RequestMarkerContext {
  import net.logstash.logback.marker.Markers

  private def marker(tuple: (String, Any)) = Markers.append(tuple._1, tuple._2)

  private implicit class RichLogstashMarker(marker1: LogstashMarker) {
    def &&(marker2: LogstashMarker): LogstashMarker = marker1.and(marker2)
  }

  implicit def requestHeaderToMarkerContext(implicit request: RequestHeader): MarkerContext = {
    MarkerContext {
      marker("id" -> request.id) && marker("host" -> request.host) && marker("remoteAddress" -> request.remoteAddress)
    }
  }
}

class PostActionBuilder @Inject()(messagesApi: MessagesApi, playBodyParsers: PlayBodyParsers)
                                 (implicit val executionContext: ExecutionContext)
  extends ActionBuilder[PostRequest, AnyContent]
  with RequestMarkerContext
  with HttpVerbs {

  private val logger = Logger(getClass)

  val parser: BodyParser[AnyContent] = playBodyParsers.anyContent

  type PostRequestBlock[A] = PostRequest[A] => Future[Result]

  override def invokeBlock[A](request: Request[A],
                              block: PostRequestBlock[A]): Future[Result] = {

    implicit val markerContext: MarkerContext = requestHeaderToMarkerContext(request)

    logger.trace("invokeBlock: ")
    val future = block(new PostRequest(request, messagesApi))

    future.map { result =>
      request.method match {
        case GET | HEAD =>
          result.withHeaders("Cache-Control" -> s"max-age: 100")
        case other =>
          result
      }
    }

  }
}

case class PostControllerComponents @Inject()(postActionBuilder: PostActionBuilder,
                                              postResourceHandler: PostResourceHandler,
                                              actionBuilder: DefaultActionBuilder,
                                              parsers: PlayBodyParsers,
                                              messagesApi: MessagesApi,
                                              langs: Langs,
                                              fileMimeTypes: FileMimeTypes,
                                              executionContext: scala.concurrent.ExecutionContext
                                             )
  extends ControllerComponents

class PostBaseController @Inject()(pcc: PostControllerComponents) extends BaseController with RequestMarkerContext {
  override protected def controllerComponents: ControllerComponents = pcc

  def PostAction: PostActionBuilder = pcc.postActionBuilder

  def postResourceHandler: PostResourceHandler = pcc.postResourceHandler
}