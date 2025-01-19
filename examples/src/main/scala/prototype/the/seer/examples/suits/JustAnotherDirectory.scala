package prototype.the.seer.examples.suits

import prototype.the.seer.api.archModel
import JustAnotherDirectory.JustDao

object JustAnotherDirectory {
  class JustService

  class JustDao
}

object Somewhere {
  @archModel[JustDao]
  class MyModelForDao(str: String, int: Int) {
    def doStuff(x: Int): Long = x.toLong
  }
}
