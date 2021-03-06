import com.google.inject.AbstractModule
import java.time.Clock
import javax.inject._

import net.codingwell.scalaguice.ScalaModule
import play.api.{Configuration, Environment}
import services.{ApplicationTimer, AtomicCounter, Counter}
import v1.post.{PostRepository, PostRepositoryImpl}

/**
 * This class is a Guice module that tells Guice how to bind several
 * different types. This Guice module is created when the Play
 * application starts.

 * Play will automatically use any class called `Module` that is in
 * the root package. You can create modules in other locations by
 * adding `play.modules.enabled` settings to the `application.conf`
 * configuration file.
 */
class Module(environment: Environment, configuration: Configuration)
  extends AbstractModule
  with ScalaModule {

  override def configure() = {
    // Use the system clock as the default implementation of Clock
    bind(classOf[Clock]).toInstance(Clock.systemDefaultZone)
    // Ask Guice to create an instance of ApplicationTimer when the
    // application starts.
    bind(classOf[ApplicationTimer]).asEagerSingleton()
    // Set AtomicCounter as the implementation for Counter.
    bind(classOf[Counter]).to(classOf[AtomicCounter])

    bind[PostRepository].to[PostRepositoryImpl].in[Singleton]
  }

}
