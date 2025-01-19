package prototype.the.seer.compiler.plugin.utils

import prototype.the.seer.api.model.{Database, ExternalService, Kafka, RedisAuto, RedisCustom}
import prototype.the.seer.api.model._

import scala.tools.nsc.Global

trait MetaInlining {
  val global: Global

  import global._

  // Для вырожденных случаев, когда все аргументы аннотации заданы поименно, при этом в неправильно порядке
  // @arch(Database(dbType = Oracle,  comment = Some("comment 2"), name = "my db ex 2"))
  // case class Ex2(str: String)
  //{
  //  <artifact> val x$1: prototype.the.seer.api.model.Database.Postgres.type = prototype.the.seer.api.model.Database.Postgres;
  //  <artifact> val x$2: String("my db ex 4") = "my db ex 4";
  //  <artifact> val x$3: Option[String] @scala.reflect.internal.annotations.uncheckedBounds = prototype.the.seer.api.model.Database.apply$default$3;
  //  prototype.the.seer.api.model.Database.apply("my db ex 4", x$1, x$3)
  //}
  def matchOnAnnotationArgument(
      tree: Tree,
      className: Name,
      typeParams: Vector[String]
  ) = {
    tree match {
      case tr @ q"""prototype.the.seer.api.model.$annotationParamName.apply(..$args)""" =>
        enrichModel(annotationParamName, args, className, typeParams)

      case tr @ q"""prototype.the.seer.api.model.$annotationParamName""" =>
        enrichModel(annotationParamName, Seq.empty, className, typeParams)

      case tr @ q"""{
            ..$params1 val $x1: $x1Type = $x1Body;
            prototype.the.seer.api.model.$annotationParamName.apply(..$args)
            }""" =>
        val mappedArgs = mergeProxies(
          args.asInstanceOf[Seq[Tree]],
          Map(
            x1.toString() -> x1Body
          )
        )

        enrichModel(annotationParamName, mappedArgs, className, typeParams)
      case tr @ q"""{
            ..$params1 val $x1: $x1Type = $x1Body;
            ..$params2 val $x2: $x2Type = $x2Body;
            prototype.the.seer.api.model.$annotationParamName.apply(..$args)
            }""" =>
        val mappedArgs = mergeProxies(
          args.asInstanceOf[Seq[Tree]],
          Map(
            x1.toString() -> x1Body,
            x2.toString() -> x2Body
          )
        )

        enrichModel(annotationParamName, mappedArgs, className, typeParams)
      case tr @ q"""{
            ..$params1 val $x1: $x1Type = $x1Body;
            ..$params2 val $x2: $x2Type = $x2Body;
            ..$params3 val $x3: $x3Type = $x3Body;
            prototype.the.seer.api.model.$annotationParamName.apply(..$args)
             }""" =>
        val mappedArgs = mergeProxies(
          args.asInstanceOf[Seq[Tree]],
          Map(
            x1.toString() -> x1Body,
            x2.toString() -> x2Body,
            x3.toString() -> x3Body
          )
        )
        enrichModel(annotationParamName, mappedArgs, className, typeParams)
      case tr @ q"""{
            ..$params1 val $x1: $x1Type = $x1Body;
            ..$params2 val $x2: $x2Type = $x2Body;
            ..$params3 val $x3: $x3Type = $x3Body;
            ..$params4 val $x4: $x4Type = $x4Body;
            prototype.the.seer.api.model.$annotationParamName.apply(..$args)
            }""" =>
        val mappedArgs = mergeProxies(
          args.asInstanceOf[Seq[Tree]],
          Map(
            x1.toString() -> x1Body,
            x2.toString() -> x2Body,
            x3.toString() -> x3Body,
            x4.toString() -> x4Body
          )
        )
        enrichModel(annotationParamName, mappedArgs, className, typeParams)
      case tr @ q"""{
           ..$params1 val $x1: $x1Type = $x1Body;
           ..$params2 val $x2: $x2Type = $x2Body;
           ..$params3 val $x3: $x3Type = $x3Body;
           ..$params4 val $x4: $x4Type = $x4Body;
           ..$params5 val $x5: $x5Type = $x5Body;
           prototype.the.seer.api.model.$annotationParamName.apply(..$args)
           }""" =>
        val mappedArgs = mergeProxies(
          args.asInstanceOf[Seq[Tree]],
          Map(
            x1.toString() -> x1Body,
            x2.toString() -> x2Body,
            x3.toString() -> x3Body,
            x4.toString() -> x4Body,
            x5.toString() -> x5Body
          )
        )
        enrichModel(annotationParamName, mappedArgs, className, typeParams)
      case other => throw new Throwable(s"Parsing failed on:\n${other.toString()}")
    }
  }

  private def enrichModel(
      model: TermName,
      args: Seq[Tree],
      name: Name,
      typeParams: Vector[String]
  ) =
    model.toString() match {
      case Database.className =>
        q"""prototype.the.seer.api.model.enriched.DatabaseEnriched.apply(..$args)"""
      case Kafka.className =>
        q"""prototype.the.seer.api.model.enriched.KafkaEnriched.apply(..$args)"""
      case RedisAuto.className =>
        if (typeParams.size != 2)
          throw new Throwable(
            s"RedisAutoMeta has to be applied to traits/classes with exactly two type params.\nProvided: $typeParams"
          )
        q"prototype.the.seer.api.model.enriched.RedisEnriched(cacheName = ${name
          .toString()}, keyType = ${typeParams(0)
          .toString()}, valueType = ${typeParams(1).toString()})"
      case RedisCustom.className =>
        q"prototype.the.seer.api.model.enriched.RedisEnriched(..$args)"
      case ExternalService.className =>
        q"""prototype.the.seer.api.model.enriched.ExternalServiceEnriched.apply(..$args)"""
      case unknown => throw new Throwable(s"Not found match on the following name: $unknown")
    }

  private def mergeProxies(args: Seq[Tree], proxies: Map[String, Tree]) =
    args.map(arg => proxies.getOrElse(arg.toString(), arg))
}
