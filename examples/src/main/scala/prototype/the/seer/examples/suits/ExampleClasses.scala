package prototype.the.seer.examples.suits

import prototype.the.seer.api.model.Database.{Oracle, Postgres}
import prototype.the.seer.api.model.Kafka._
import prototype.the.seer.api.model._
import prototype.the.seer.api.{arch, archModel}
import JustAnotherDirectory.{JustDao, JustService}

import scala.concurrent.ExecutionContext

object `We have an object` {
  @arch(
    RedisCustom(cacheName = "firstExample", keyType = "User ID", valueType = "User roles")
  )
  object RedisModule
}

object `We have a case class declaration with double arch annotation` {
  @arch(
    Kafka(
      topic = "user.changed",
      env = Env.Test,
      direction = Direction.Consumer
    )
  )
  @arch(
    Kafka(
      topic = "retry.user.changed",
      env = Env.Prod,
      direction = Direction.Consumer,
      comment = Some("""/╲/\\╭༼ ººل͟ºº ༽╮/\\╱\\""")
    )
  )
  case class KafkaImpl()
}

object `We have a trait declaration and its model from somewhere` {
  @arch(Database(name = "USER_PRODUCTS", dbType = Postgres))
  trait DatabaseDAO

  object Somewhere {
    trait InsideDeepTrait {
      @archModel[DatabaseDAO]
      case class DatabaseModel(id: Int, productId: String)
    }
  }
}

object `We have a val declaration` {
  @arch(ExternalService("UserInfo")) val service = new JustService
}

object `We have a crazy def declaration` {
  @arch(ExternalService("UserPhones")) private def service[Whatever](str: String)(int: Int) =
    new JustService
}

object `We have trait that fits RedisAuto conditions` {
  @arch(RedisAuto)
  trait Redis[Key, Set[String]]
}

object `We have a complicated val that fits RedisAuto conditions` {

  trait Redis[Key, Value]

  class RedisImpl extends Redis[Long, Set[Set[String]]]

  @arch(RedisAuto) val myRedis: Redis[Long, Set[Set[String]]] = new RedisImpl
}

object `We have a val declaration an its corresponding model (see JustAnotherDirectory)` {
  @arch(Database(dbType = Oracle, comment = Some("comment 2"), name = "USER_CROSSPRODUCT")) val myDaoImpl =
    new JustDao
}

object `We have a trait inside trait declaration` {
  trait A {
    trait B {
      trait C {
        @arch(ExternalService("Products")) trait ProductsClient[F[_]]
      }
    }
  }
}

object `We have a class declaration inside expression` {
  val expr = {
    @arch(
      ExternalService(
        externalSystem = "Auth",
        comment = Some("The client for the system contains in-memory cache, beware!")
      )
    )
    class SsoClient

    new SsoClient {}

    42
  }

  val _ = expr
}

object `We have a class with implicit args in contructor` {
  val topic = "user.events"
  val env = Env.Test
  val direction = Direction.Consumer

  @arch(Kafka(topic = topic, env = env, direction = direction, comment = None))
  case class KafkaConsumer(topic: String)(implicit ec: ExecutionContext)
}
