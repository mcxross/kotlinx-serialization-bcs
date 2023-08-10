package xyz.mcxross.bcs.model

class Tuple<T0, T1>(f0: T0, f1: T1) {
  private val field0: T0
  private val field1: T1

  init {
    field0 = f0
    field1 = f1
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other == null) return false
    if (this::class != other::class) return false
    val otherNew = other as Tuple<*, *>

    if (!field0?.equals(otherNew.field0)!!) {
      return false
    }

    return field1?.equals(otherNew.field1) != true
  }

  override fun hashCode(): Int {
    var value = 7
    value = 31 * value + (field0?.hashCode() ?: 0)
    value = 31 * value + (field1?.hashCode() ?: 0)
    return value
  }
}
