package prototype.the.seer.api.model.enriched

import prototype.the.seer.api.model.Database.DatabaseType
import prototype.the.seer.api.model.Field
import prototype.the.seer.api.model.Kafka.{Direction, Env}
import prototype.the.seer.api.model.ArchGraph.{BelongsToFQCN, ModelSimpleName}
import prototype.the.seer.api.model.Database.DatabaseType
import prototype.the.seer.api.model.Kafka._

sealed trait MetaInfoEnriched

case class DatabaseEnriched(
    name: String,
    dbType: DatabaseType,
    comment: Option[String]
) extends MetaInfoEnriched

case class KafkaEnriched(
    topic: String,
    env: Env,
    direction: Direction,
    comment: Option[String] = None
) extends MetaInfoEnriched

case class ExternalServiceEnriched(
    externalSystem: String,
    comment: Option[String]
) extends MetaInfoEnriched

case class RedisEnriched(
    cacheName: String,
    keyType: String,
    valueType: String,
    comment: Option[String] = None
) extends MetaInfoEnriched

case class ArchModelEnriched(
    modelName: ModelSimpleName,
    belongsTo: BelongsToFQCN,
    fields: Set[Field]
)
