package prototype.the.seer.api

import prototype.the.seer.api.model.{ArchGraph, MetaInfo}

import scala.annotation.{StaticAnnotation, compileTimeOnly, nowarn}

class arch(@nowarn meta: MetaInfo) extends StaticAnnotation
class archModel[BelongsTo] extends StaticAnnotation

object TheSeerAPI {
  @compileTimeOnly("Should be erased during compile time!")
  def summonMeta: ArchGraph = ???
}
