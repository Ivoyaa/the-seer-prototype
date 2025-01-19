package prototype.the.seer.compiler.plugin

import prototype.the.seer.compiler.plugin.component.{TheSeerMetaCollector, TheSeerMetaInliner}
import prototype.the.seer.compiler.plugin.utils.ArchGraphInner

import scala.tools.nsc.Global
import scala.tools.nsc.plugins.{Plugin, PluginComponent}

class TheSeer(val global: Global) extends Plugin {

  override val name: String = "the-seer"

  val graph: ArchGraphInner = new ArchGraphInner

  override val components: List[PluginComponent] = {
    new TheSeerMetaInliner(global, graph) ::
      new TheSeerMetaCollector(global, graph) :: Nil
  }

  override def init(options: List[String], error: String => Unit): Boolean = true

  override val description: String = """"/╲/\\╭༼ ººل͟ºº ༽╮/\\╱\\"""
}
