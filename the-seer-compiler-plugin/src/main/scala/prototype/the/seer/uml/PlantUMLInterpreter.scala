package prototype.the.seer.uml

import prototype.the.seer.api.model.ArchGraph
import prototype.the.seer.api.model.Database.{Oracle, Postgres}
import prototype.the.seer.api.model.Kafka.Direction.{Consumer, Producer}
import prototype.the.seer.api.model.Kafka.Env.Prod
import prototype.the.seer.core.{Interpreter, SetMap}
import prototype.the.seer.uml.model.{
  AgentsInner,
  DatabaseOracle,
  DatabasePostgres,
  KafkaConsumer,
  KafkaProducer,
  MetaInfoShortened,
  NodesInner,
  Redis
}
import prototype.the.seer.api.model.Database.{Oracle, Postgres}
import prototype.the.seer.api.model.Kafka.Direction._
import prototype.the.seer.api.model.Kafka.Env.Prod
import prototype.the.seer.api.model.enriched._
import model._
import prototype.the.seer.api.model.enriched.{
  DatabaseEnriched,
  ExternalServiceEnriched,
  KafkaEnriched,
  RedisEnriched
}

case class PlantUML(uml: String) {
  require(requirement = uml.contains("@startuml"), message = "No @startuml is provided!")
  require(requirement = uml.contains("@enduml"), message = "No @enduml is provided!")
}

// TODO сделать интерпретатор, заточенный под md. Чтобы агенты для каждой ноды рендерились отдельно
class PlantUMLInterpreter(systemName: String) extends Interpreter[PlantUML] {
  def interpretGraph(archGraph: ArchGraph): PlantUML = {
    val NodesInner(nodeStructure, nodes) = makeNodes(archGraph)
    val AgentsInner(agentsStructure, agents) = makeAgents(archGraph)
    PlantUML(
      "@startuml\n" + nodeStructure + agentsStructure + makeLinks(
        agents,
        nodes
      ) + "\n@enduml"
    )
  }

  private def makeNodes(archGraph: ArchGraph): NodesInner =
    archGraph.dependencies.foldLeft(NodesInner("", Set.empty[MetaInfoShortened])) {
      case (acc, (_, meta)) =>
        meta match {
          case KafkaEnriched(_, Prod, Producer, _)
              if !isDuplicateNode(acc.renderedNodeStructure, KafkaProducer) =>
            NodesInner(
              renderedNodeStructure = acc.renderedNodeStructure + "\n" + s"""
                                                                            |queue ${KafkaProducer.name} [
                                                                            |  Kafka Producer
                                                                            |]
                                                                            |""".stripMargin,
              nodesSet = acc.nodesSet ++ Set(KafkaProducer)
            )
          case KafkaEnriched(_, Prod, Consumer, _)
              if !isDuplicateNode(acc.renderedNodeStructure, KafkaConsumer) =>
            NodesInner(
              renderedNodeStructure = acc.renderedNodeStructure + "\n" + s"""
                                                                            |queue ${KafkaConsumer.name} [
                                                                            |  Kafka Consumer
                                                                            |]
                                                                            |""".stripMargin,
              nodesSet = acc.nodesSet ++ Set(KafkaConsumer)
            )
          case DatabaseEnriched(_, Oracle, _)
              if !isDuplicateNode(acc.renderedNodeStructure, DatabaseOracle) =>
            NodesInner(
              renderedNodeStructure = acc.renderedNodeStructure + "\n" + s"""
                                                                            |database ${DatabaseOracle.name} [
                                                                            |  Database Oracle
                                                                            |]
                                                                            |""".stripMargin,
              nodesSet = acc.nodesSet ++ Set(DatabaseOracle)
            )
          case DatabaseEnriched(_, Postgres, _)
              if !isDuplicateNode(acc.renderedNodeStructure, DatabasePostgres) =>
            NodesInner(
              renderedNodeStructure = acc.renderedNodeStructure + "\n" + s"""
                                                                            |database ${DatabasePostgres.name} [
                                                                            |  Database Postgres
                                                                            |]
                                                                            |""".stripMargin,
              nodesSet = acc.nodesSet ++ Set(DatabasePostgres)
            )
          case ExternalServiceEnriched(externalSystem, _) =>
            NodesInner(
              renderedNodeStructure = acc.renderedNodeStructure + "\n" + s"""
                                                                            |rectangle ${prepareName(
                externalSystem
              )} [
                                                                            |  Service $externalSystem
                                                                            |]
                                                                            |""".stripMargin,
              nodesSet = acc.nodesSet ++ Set(Service(prepareName(externalSystem)))
            )
          case RedisEnriched(cacheName, _, _, _)
              if !isDuplicateNode(acc.renderedNodeStructure, Redis) =>
            NodesInner(
              renderedNodeStructure = acc.renderedNodeStructure + "\n" + s"""
                                                                            |storage ${Redis.name} [
                                                                            |  Redis $cacheName
                                                                            |]
                                                                            |""".stripMargin,
              nodesSet = acc.nodesSet ++ Set(Redis)
            )
          case _ => acc
        }
    }

  private def makeAgents(archGraph: ArchGraph): AgentsInner =
    archGraph.toDependencies.foldLeft(AgentsInner("", SetMap.empty[MetaInfoShortened, String])) {
      case (acc, dependency) =>
        dependency.dependencyMeta match {
          case KafkaEnriched(topic, Prod, Producer, _) =>
            acc.updateAgents(prepareName(topic), KafkaProducer)
          case KafkaEnriched(topic, Prod, Consumer, _) =>
            acc.updateAgents(prepareName(topic), KafkaConsumer)
          case DatabaseEnriched(name, Oracle, _) =>
            acc.updateAgents(prepareName(name), DatabaseOracle)
          case DatabaseEnriched(name, Postgres, _) =>
            acc.updateAgents(prepareName(name), DatabasePostgres)
          case RedisEnriched(cacheName, _, _, _) =>
            acc.updateAgents(prepareName(cacheName), Redis)
          case _ => acc
        }
    }

  private def makeLinks(
      agents: SetMap[MetaInfoShortened, String],
      nodes: Set[MetaInfoShortened]
  ): String = nodes.foldLeft(s"""
                                |node $systemName [
                                |  $systemName
                                |]
                                |""".stripMargin) { case (acc, node) =>
    node match {
      case Service(name) =>
        acc +
          s"""
             |$systemName-->${name}
             |""".stripMargin
      case DatabasePostgres | DatabaseOracle | Redis =>
        acc +
          s"""
             |$systemName--->${node.name}
             |""".stripMargin + agents.getSafely(node).foldLeft("") { case (accAgents, agent) =>
            accAgents +
              s"""
                 |${node.name}-->$agent
                 |""".stripMargin
          }
      case KafkaConsumer | KafkaProducer =>
        acc +
          s"""
             |$systemName----->${node.name}
             |""".stripMargin + agents.getSafely(node).foldLeft("") { case (accAgents, agent) =>
            accAgents +
              s"""
                 |${node.name}-->$agent
                 |""".stripMargin
          }
    }

  }

  private def isDuplicateNode(body: String, metaShortened: MetaInfoShortened): Boolean =
    body.contains(metaShortened.name)

  // TODO regex for special chars
  private def prepareName(nameRaw: String) =
    nameRaw.replace(" ", "_").replace("-", "_")
}
