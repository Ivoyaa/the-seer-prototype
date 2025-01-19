package prototype.the.seer.uml

import prototype.the.seer.core.SetMap

private[uml] object model {

  case class NodesInner(renderedNodeStructure: String, nodesSet: Set[MetaInfoShortened])

  case class AgentsInner(
      renderedAgentStructure: String,
      agentsMap: SetMap[MetaInfoShortened, String]
  ) {
    def updateAgents(preparedName: String, agentType: MetaInfoShortened): AgentsInner = AgentsInner(
      renderedAgentStructure = renderedAgentStructure +
        s"""
           |agent $preparedName
           |""".stripMargin,
      agentsMap = agentsMap + (agentType -> preparedName)
    )
  }

  abstract class MetaInfoShortened(val name: String)
  case object DatabaseOracle extends MetaInfoShortened("Database_Oracle_Struct")
  case object DatabasePostgres extends MetaInfoShortened("Database_Postgres_Struct")
  case object KafkaProducer extends MetaInfoShortened("Kafka_Producer_Struct")
  case object KafkaConsumer extends MetaInfoShortened("Kafka_Consumer_Struct")
  case object Redis extends MetaInfoShortened("Redis_Struct")
  case class Service(override val name: String) extends MetaInfoShortened(name)
}
