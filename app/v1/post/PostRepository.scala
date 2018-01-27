package v1.post

import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import play.api.{Logger, MarkerContext}
import play.api.libs.concurrent.CustomExecutionContext

import scala.concurrent.Future

final case class PostData(id: PostId, title: String, body: String)

class PostId private (val underlying: Int) extends AnyVal {
  override def toString: String = underlying.toString
}

object PostId {
  def apply(raw: String): PostId = {
    require(raw != null)
    new PostId(Integer.parseInt(raw))
  }
}

class PostExecutionContext @Inject()(actorSystem: ActorSystem) extends CustomExecutionContext(actorSystem, "repository.dispatcher")

trait PostRepository {
  def create(data: PostData)(implicit mc: MarkerContext): Future[PostId]

  def list()(implicit mc: MarkerContext): Future[Iterable[PostData]]

  def get(id: PostId)(implicit mc: MarkerContext): Future[Option[PostData]]
}

@Singleton
class PostRepositoryImpl @Inject()(implicit ec: PostExecutionContext) extends PostRepository {

  private val logger = Logger(getClass)

  private val postList = List (
    PostData(PostId("1"), "title 1", "blog post 1"),
    PostData(PostId("2"), "title 2", "blog post 2"),
    PostData(PostId("3"), "title 3", "blog post 3"),
    PostData(PostId("4"), "title 4", "blog post 4")
  )

  override def list()(implicit mc: MarkerContext): Future[Iterable[PostData]] = {
    Future {
      logger.trace("list: ")
      postList
    }
  }

  override def get(id: PostId)(implicit mc: MarkerContext): Future[Option[PostData]] = {
    Future {
      logger.trace(s"get: id = $id")
      postList.find(post => post.id == id)
    }
  }

  override def create(data: PostData)(implicit mc: MarkerContext): Future[PostId] = {
    Future {
      logger.trace(s"create: data = $data")
      data.id
    }
  }
}