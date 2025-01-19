package prototype.the.seer.core

class SetMap[A, B](val innerMap: Map[A, Set[B]]) {
  def +(keyValue: (A, B)): SetMap[A, B] = {
    val (key, value) = keyValue
    val concatenated =
      if (innerMap.contains(key)) innerMap.updated(key, innerMap(key) ++ Set(value))
      else innerMap + (key -> Set(value))

    new SetMap[A, B](concatenated)
  }

  def getSafely(key: A): Set[B] = innerMap.getOrElse(key, Set.empty)
}

object SetMap {
  def empty[A, B] = new SetMap[A, B](Map.empty[A, Set[B]])
}
