package prototype.the.seer.compiler.plugin.utils

import scala.tools.nsc.Global

trait Reflection extends MetaInlining {

  val global: Global
  import global._

  def refineClassDefToArchModelEnriched(classOrModuleDef: ImplDef): Set[Tree] = {
    val annotations =
      classOrModuleDef.symbol.annotations
        .filter(_.symbol.fullName == "prototype.the.seer.api.archModel")

    val (_, className, fieldsRaw: Seq[Tree], _, _, _) = classOrModuleDef match {
      case q"..$mods class $className[..$typeParams](..$fields) extends ..$parents { ..$body }" =>
        (mods, className, fields, parents, body, typeParams.toVector.map(_.tpe.toString))
      case other => throw new UnmatchedTreeException(other.toString())
    }

    val fields = extractFields(fieldsRaw)
    // есть еще вариант .tpe.fullName, но некосистентно с тем, как рефайнятся модели, когда они лежат в объектах с названиями в кавычках, как `my object`
    val extractBelongsToFQCN = (annotation: AnnotationInfo) =>
      annotation.tpe.typeArgs.head.typeSymbol.fullName
    val modelSimpleName = className.toString()

    annotations
      .map(annotation =>
        q"prototype.the.seer.api.model.enriched.ArchModelEnriched(modelName = $modelSimpleName, belongsTo = ${extractBelongsToFQCN(annotation)}, fields = $fields )"
      )
      .toSet
  }

  def refineImplDefToMetaInfoEnriched(classOrModuleDef: ImplDef): Set[Tree] = {
    val annotations =
      classOrModuleDef.symbol.annotations
        .filter(_.symbol.fullName == "prototype.the.seer.api.arch")

    val (_, name, _, _, _, typeParams) = classOrModuleDef match {
      case q"..$mods class $className[..$typeParams](..$fields) extends ..$parents { ..$body }" =>
        (mods, className, fields, parents, body, typeParams.toVector.map(_.tpe.toString))
      case q"..$mods class $className[..$typeParams](..$fields)(..$implicitFields) extends ..$parents { ..$body }" =>
        (mods, className, fields, parents, body, typeParams.toVector.map(_.tpe.toString))
      //здесь проблема с typeParams.map(_.tpe), так как это, оказывается, не type-ы
      case q"..$mods trait $traitName[..$typeParams] extends ..$parents { ..$body }" =>
        (
          mods,
          traitName,
          Seq.empty,
          parents,
          body,
          typeParams.toVector.map(typeName => typeName.toString().replace("type ", ""))
        )
      case q"..$mods object $objectName extends ..$parents { ..$body }" =>
        (mods, objectName, Seq.empty, parents, body, Vector.empty)
      case other => throw new UnmatchedTreeException(other.toString())
    }

    val enriched = annotations.map(annotation =>
      matchOnAnnotationArgument(annotation.args.head, name, typeParams)
    )

    enriched.toSet
  }

  def refineValOrDefToMetaInfoEnriched(valOrDef: ValOrDefDef): Set[Tree] = {
    val annotations =
      valOrDef.symbol.annotations
        .filter(_.symbol.fullName == "prototype.the.seer.api.arch")

    val (name, className, typeParams) = valOrDef match {
      case q"..$mods val $name: $className[..$typeParams] = {..$body} " if typeParams.nonEmpty =>
        (name, className.symbol.name, typeParams.toVector.map(_.tpe.toString))
      case q"..$mods val $name: $classNameWithTypes = {..$body}" =>
        (
          name,
          classNameWithTypes.symbol.name,
          classNameWithTypes.tpe.typeArgs.toVector.map(_.toString)
        )
      case q"..$mods def $name[..$typeArgs](..$args): $className[..$typeParams] = {..$body} "
          if typeParams.nonEmpty =>
        (name, className.symbol.name, typeParams.toVector.map(_.tpe.toString))
      case q"..$mods def $name[..$typeArgs](..$args1)(..$args2): $className[..$typeParams] = {..$body} "
          if typeParams.nonEmpty =>
        (name, className.symbol.name, typeParams.toVector.map(_.tpe.toString))
      case q"..$mods def $name[..$typeArgs](..$args1)(..$args2)(..$args3): $className[..$typeParams] = {..$body} "
          if typeParams.nonEmpty =>
        (name, className.symbol.name, typeParams.toVector.map(_.tpe.toString))
      case q"..$mods def $name[..$typeArgs](..$args): $classNameWithTypes = {..$body}" =>
        (
          name,
          classNameWithTypes.symbol.name,
          classNameWithTypes.tpe.typeArgs.toVector.map(_.toString)
        )
      case q"..$mods def $name[..$typeArgs](..$args1)(..$args2): $classNameWithTypes = {..$body}" =>
        (
          name,
          classNameWithTypes.symbol.name,
          classNameWithTypes.tpe.typeArgs.toVector.map(_.toString)
        )
      case q"..$mods def $name[..$typeArgs](..$args1)(..$args2)(..$args3): $classNameWithTypes = {..$body}" =>
        (
          name,
          classNameWithTypes.symbol.name,
          classNameWithTypes.tpe.typeArgs.toVector.map(_.toString)
        )
      case other => throw new UnmatchedTreeException(other.toString())
    }

    val enriched = annotations.map(annotation =>
      matchOnAnnotationArgument(annotation.args.head, name, typeParams)
    )

    enriched.toSet
  }

  private def extractFields(fields: Seq[Tree]) = {
    fields
      .asInstanceOf[List[ValDef]]
      .flatMap {
        case q"$accessor val $vname: $tpe" =>
          Some(
            q"prototype.the.seer.api.model.Field(name = ${vname.toString()}, tpe = ${tpe.toString()})"
          )
        case q"$accessor val $vname: $tpe = $default" =>
          Some(
            q"prototype.the.seer.api.model.Field(name = ${vname.toString()}, tpe = ${tpe.toString()})"
          )
      }
      .toSet
  }

  private class UnmatchedTreeException(tree: String)
      extends RuntimeException(
        s"Got unexpected pattern of code tree under annotation: $tree"
      )
}
