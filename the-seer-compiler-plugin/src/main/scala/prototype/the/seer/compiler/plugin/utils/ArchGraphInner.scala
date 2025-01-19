package prototype.the.seer.compiler.plugin.utils

import scala.collection.mutable

class ArchGraphInner {
  private val dependencies: mutable.Set[(String, Any)] = mutable.Set.empty
  private val models: mutable.Set[Any] = mutable.Set.empty

  def putDependency(className: String, metaTree: Any): Boolean =
    dependencies.add(className -> metaTree)
  def getDependencies: mutable.Set[(String, Any)] = dependencies
  def putModel(archModelEnrichedTree: Any): Boolean =
    models.add(archModelEnrichedTree)
  def getModels: mutable.Set[Any] = models
}
