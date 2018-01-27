package v1.post

import com.google.inject.{Inject, Provider}
import play.api.MarkerContext
import play.api.libs.json.{JsValue, Json, Writes}

import scala.concurrent.{ExecutionContext, Future}

case class PostResource(id: String, link: String, title: String, body: String)

object PostResource {
  implicit val implicitWrites = new Writes[PostResource] {
    def writes(post: PostResource): JsValue = {
      Json.obj(
        "id" -> post.id,
        "link" -> post.link,
        "title" -> post.title,
        "body" -> post.body
      )
    }
  }
}

class PostResourceHandler @Inject()(routerProvider: Provider[PostRouter],
                                    postRepository: PostRepository)(implicit ec: ExecutionContext){
  def find(implicit mc: MarkerContext): Future[Iterable[PostResource]] = {
    postRepository.list().map { postDataList =>
      postDataList.map (postData => createPostResource(postData))
    }
  }

  def create(implicit mc: MarkerContext): Future[PostResource] = {
    postRepository
  }

  private def createPostResource(p: PostData): PostResource ={
    PostResource(p.id.toString, routerProvider.get.link(p.id), p.title, p.body)
  }
}
