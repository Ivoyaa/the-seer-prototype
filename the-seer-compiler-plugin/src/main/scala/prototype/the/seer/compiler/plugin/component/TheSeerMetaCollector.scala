package prototype.the.seer.compiler.plugin.component

import prototype.the.seer.compiler.plugin.utils.{ArchGraphInner, Reflection}
import prototype.the.seer.compiler.plugin.utils._

import scala.tools.nsc.plugins.PluginComponent
import scala.tools.nsc.{Global, Phase}

class TheSeerMetaCollector(val global: Global, graph: ArchGraphInner)
    extends PluginComponent
    with Reflection {

  import global._

  override val runsAfter = "typer" :: Nil
  override val phaseName = "meta-collector"

  val currentDir = System.getProperty("user.dir")

  def newPhase(prev: Phase) = new StdPhase(prev) {

    override def run(): Unit = {
      val currentModule = {
        val modulePart = global.currentRun.units.next.source.path.substring(currentDir.length + 1)
        modulePart.split("/", 2).headOption.getOrElse(modulePart)
      }
      reporter.echo("Collecting meta for module " + currentModule)

      super.run()
    }

    class CallerTraverser(val oldCaller: String) extends Traverser {
      override def traverse(tree: Tree): Unit = {
        tree match {
          case cd: ClassDef
              if cd.symbol.annotations
                .exists(info => info.symbol.fullName == "prototype.the.seer.api.arch") =>
            val metasEnriched = refineImplDefToMetaInfoEnriched(cd)

            metasEnriched.foreach(metaEnriched =>
              graph.putDependency(cd.symbol.fullName, metaEnriched)
            )

            super.traverse(cd.impl)

          case md: ModuleDef
              if md.symbol.annotations
                .exists(info => info.symbol.fullName == "prototype.the.seer.api.arch") =>
            val metasEnriched = refineImplDefToMetaInfoEnriched(md)

            metasEnriched.foreach(metaEnriched =>
              graph.putDependency(md.symbol.fullName, metaEnriched)
            )

            super.traverse(md.impl)

          case valOrDef: ValOrDefDef
              if valOrDef.symbol.annotations
                .exists(info => info.symbol.fullName == "prototype.the.seer.api.arch") =>
            val metasEnriched = refineValOrDefToMetaInfoEnriched(valOrDef)

            metasEnriched.foreach(metaEnriched =>
              graph.putDependency(valOrDef.rhs.tpe.typeSymbol.fullName, metaEnriched)
            )

            super.traverse(valOrDef.rhs)

          case cd: ClassDef
              if cd.symbol.annotations
                .exists(info => info.symbol.fullName == "prototype.the.seer.api.archModel") =>
            val archModelsEnriched = refineClassDefToArchModelEnriched(cd)

            archModelsEnriched.foreach(archModelEnriched => graph.putModel(archModelEnriched))

            super.traverse(cd.impl)

          case tr =>
            super.traverse(tr)
        }
      }
    }

    override def apply(unit: CompilationUnit): Unit = {
      new CallerTraverser("root").traverse(unit.body)
    }
  }
}
