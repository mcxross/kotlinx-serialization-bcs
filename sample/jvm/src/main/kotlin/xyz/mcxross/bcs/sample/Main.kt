package xyz.mcxross.bcs.sample

import kotlinx.serialization.Serializable
import xyz.mcxross.bcs.Bcs

@Serializable
data class Data(val long: Long, val double: Double, val boolean: Boolean, val string: String)

fun main() {

  val bcs = Bcs {}

  val encoding = bcs.encodeToByteArray(Data(1_000_000, 3.14159265359, false, "çå∞≠¢õß∂ƒ∫"))

  println("Encoding: ${encoding.toList()}")

  val decoding = bcs.decodeFromByteArray<Data>(encoding)

  println("Decoding: $decoding")
}
