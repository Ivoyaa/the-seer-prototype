package prototype.the.seer.core

import prototype.the.seer.api.model.{ArchGraph, Field}
import prototype.the.seer.api.model.enriched.MetaInfoEnriched
import ArchGraph.ModelSimpleName

trait Interpreter[Result] {
  def interpretGraph(archGraph: ArchGraph): Result
}

/*
Kafka(topic = "my.events")
      -> ProductAdded(productId, instantiator)
      -> ProductDeleted(productId, endDate)

Dependency(Kafka, Set(
  DependencyModelWithFields(RoleAdded(roleId, instantiator),
  DependencyModelWithFields(RoleExpired(roleId, endDate)
))
 */
case class Dependency(
    dependencyMeta: MetaInfoEnriched,
    dependencyModels: Set[DependencyModelWithFields]
)
case class DependencyModelWithFields(modelName: ModelSimpleName, fields: Set[Field])
