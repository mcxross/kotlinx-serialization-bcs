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
  val bcs = Bcs
  bcs.builderAction()
  return bcs
}

object Bcs {
  fun <T> encodeToBinary(serializer: SerializationStrategy<T>, value: T): ByteArray {
    val bcsEncoder = BcsEncoder()
    bcsEncoder.encodeSerializableValue(serializer, value)
    return bcsEncoder.output.toByteArray()
  }

  inline fun <reified T> encodeToBinary(value: T) = encodeToBinary(serializer(), value)

  fun <T> decodeFromBinary(bytes: ByteArray, deserializer: DeserializationStrategy<T>): T {
    val decoder = BcsDecoder(BcsDataInputBuffer(bytes))
    return decoder.decodeSerializableValue(deserializer)
  }

  inline fun <reified T> decodeFromBinary(bytes: ByteArray): T =
    decodeFromBinary(bytes, serializer())

}
