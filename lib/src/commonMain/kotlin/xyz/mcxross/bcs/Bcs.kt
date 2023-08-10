package xyz.mcxross.bcs

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.serializer
import xyz.mcxross.bcs.internal.BcsDecoder
import xyz.mcxross.bcs.internal.BcsEncoder
import xyz.mcxross.bcs.stream.BcsDataInputBuffer
import xyz.mcxross.bcs.stream.BcsDataOutputBuffer

internal const val MAX_SEQUENCE_LENGTH: UInt = 2147483647u

internal const val MAX_CONTAINER_DEPTH: UInt = 500u

fun Bcs(builderAction: Bcs.() -> Unit): Bcs {
  val bcs = Bcs()
  bcs.builderAction()
  return bcs
}

class Bcs {
  fun <T> encodeToBinary(serializer: SerializationStrategy<T>, value: T): BcsDataOutputBuffer {
    val bcsEncoder = BcsEncoder()
    bcsEncoder.encodeSerializableValue(serializer, value)
    return bcsEncoder.output
  }

  inline fun <reified T> encodeToBinary(value: T) = encodeToBinary(serializer(), value)

  fun <T> decodeFromBinary(list: List<Byte>, deserializer: DeserializationStrategy<T>): T {
    val decoder = BcsDecoder(BcsDataInputBuffer(list.toByteArray()))
    return decoder.decodeSerializableValue(deserializer)
  }

  inline fun <reified T> decodeFromBinary(list: List<Byte>): T =
    decodeFromBinary(list, serializer())

}
