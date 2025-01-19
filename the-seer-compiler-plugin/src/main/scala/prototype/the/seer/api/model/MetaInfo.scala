package prototype.the.seer.api.model

import Database.DatabaseType
import Kafka.{Direction, Env}

sealed trait MetaInfo

case class Database(name: String, dbType: DatabaseType, comment: Option[String] = None)
    extends MetaInfo
object Database {
  val className = classOf[Database].getSimpleName

  sealed trait DatabaseType

  case object Oracle extends DatabaseType

  case object Postgres extends DatabaseType
}

case class Kafka(
    topic: String,
    env: Env,
    direction: Direction,
    comment: Option[String] = None
) extends MetaInfo
object Kafka {
  val className = classOf[Kafka].getSimpleName

  sealed trait Env
  object Env {
    case object Test extends Env
    case object Prod extends Env
  }

  sealed trait Direction
  object Direction {
    case object Producer extends Direction
    case object Consumer extends Direction
  }
}

case object RedisAuto extends MetaInfo {
  val className = RedisAuto.toString
}

case class RedisCustom(
    cacheName: String,
    keyType: String,
    valueType: String,
    comment: Option[String] = None
) extends MetaInfo
object RedisCustom {
  val className = classOf[RedisCustom].getSimpleName
}

case class ExternalService(
    externalSystem: String,
    comment: Option[String] = None
) extends MetaInfo
object ExternalService {
  val className = classOf[ExternalService].getSimpleName
}

case class Field(name: String, tpe: String) {
  def prettyPrint(): String = s"- ${name}: ${tpe}"
}
