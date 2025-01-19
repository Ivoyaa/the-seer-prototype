package prototype.the.seer.api.model

import ArchGraph.BelongsToFQCN
import prototype.the.seer.api.model.enriched.{ArchModelEnriched, MetaInfoEnriched}
import prototype.the.seer.core.{Dependency, DependencyModelWithFields}

case class ArchGraph(
    dependencies: Set[(BelongsToFQCN, MetaInfoEnriched)],
    models: Set[ArchModelEnriched]
) {
  lazy val toDependencies: Set[Dependency] = dependencies.map { case (dependencyFQCN, meta) =>
    Dependency(
      dependencyMeta = meta,
      dependencyModels = models
        .filter(_.belongsTo == dependencyFQCN)
        .map(model => DependencyModelWithFields(model.modelName, model.fields))
    )
  }
}

object ArchGraph {
  type BelongsToFQCN = String
  type ModelSimpleName = String
}
