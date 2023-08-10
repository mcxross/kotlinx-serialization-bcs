package xyz.mcxross.bcs.sample

import kotlinx.serialization.Serializable
import xyz.mcxross.bcs.Bcs
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream

@Serializable
data class Data(val long: Long, val double: Double, val string: String)

fun main() {

  val bcs = Bcs {}

  val encoding = bcs.encodeToBinary(Data(1000_000, 6.5, "BCS"))

  println("Encoding: ${encoding.bytes}")

  val decoding = bcs.decodeFromBinary<Data>(encoding.bytes)

  println("Decoding: $decoding")

}
