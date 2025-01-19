package prototype.the.seer.compiler.plugin.component

import prototype.the.seer.compiler.plugin.utils.{ArchGraphInner, MetaInlining, Reflection}
import prototype.the.seer.compiler.plugin.utils._

import scala.collection.mutable
import scala.tools.nsc.Global
import scala.tools.nsc.plugins.PluginComponent
import scala.tools.nsc.transform.{Transform, TypingTransformers}

class TheSeerMetaInliner(val global: Global, graph: ArchGraphInner)
    extends PluginComponent
    with Reflection
    with MetaInlining
    with Transform
    with TypingTransformers {

  import global._

  override val runsAfter = "meta-collector" :: Nil
  override val phaseName = "meta-inliner"

  override protected def newTransformer(unit: CompilationUnit): AstTransformer =
    new TypingTransformer(unit) {

      override def transform(tree: Tree): Tree = {
        tree match {
          case (tr @ q"prototype.the.seer.api.TheSeerAPI.summonMeta") =>
            val deps = graph.getDependencies.asInstanceOf[mutable.Set[(String, Tree)]].toSet
            val models = graph.getModels.asInstanceOf[mutable.Set[Tree]].toSet

            localTyper.typed(
              q"""prototype.the.seer.api.model.ArchGraph.apply(dependencies = $deps.asInstanceOf[scala.collection.immutable.Set[(String, prototype.the.seer.api.model.enriched.MetaInfoEnriched)]], models = $models.asInstanceOf[scala.collection.immutable.Set[prototype.the.seer.api.model.enriched.ArchModelEnriched]])"""
            )
          case _ =>
            super.transform(tree)
        }
      }
    }
}
