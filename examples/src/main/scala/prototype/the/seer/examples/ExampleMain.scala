package prototype.the.seer.examples

import prototype.the.seer.api.TheSeerAPI.summonMeta
import prototype.the.seer.api.model.ArchGraph
import prototype.the.seer.uml.PlantUMLInterpreter

object ExampleMain extends App {
  val meta: ArchGraph = summonMeta

  println(new PlantUMLInterpreter("UserProductsService").interpretGraph(meta).uml)
}
