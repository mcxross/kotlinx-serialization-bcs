package xyz.mcxross.bcs

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.serializer
import xyz.mcxross.bcs.internal.BcsDecoder
import xyz.mcxross.bcs.internal.BcsEncoder
import xyz.mcxross.bcs.stream.BcsDataInputBuffer

internal const val MAX_SEQUENCE_LENGTH: UInt = 2147483647u

internal const val MAX_CONTAINER_DEPTH: UInt = 500u

fun Bcs(builderAction: Bcs.() -> Unit): Bcs {
  val bcs = Bcs
  bcs.builderAction()
  return bcs
}

object Bcs {

  /**
   * Encode a value to a byte array according to the BCS specification.
   *
   * @param serializer The serializer to use to encode the value.
   * @param value The value to encode.
   * @return The encoded value as a byte array.
   *
   */
  fun <T> encodeToByteArray(serializer: SerializationStrategy<T>, value: T): ByteArray {
    val bcsEncoder = BcsEncoder()
    bcsEncoder.encodeSerializableValue(serializer, value)
    return bcsEncoder.output.toByteArray()
  }

  /**
   * Encode a value to a byte array according to the BCS specification.
   *
   * This is an inline overload convenience method to avoid the need to explicitly pass a serializer.
   *
   * @param value The value to encode.
   * @return The encoded value as a byte array.
   *
   */
  inline fun <reified T> encodeToByteArray(value: T) =
    encodeToByteArray(serializer(), value)

  /**
   * Decode a value from a byte array according to the BCS specification.
   *
   * @param bytes The byte array to decode.
   * @param deserializer The deserializer to use to decode the value.
   * @return The decoded value.
   */
  fun <T> decodeFromByteArray(bytes: ByteArray, deserializer: DeserializationStrategy<T>): T {
    val decoder = BcsDecoder(inputBuffer = BcsDataInputBuffer(bytes))
    return decoder.decodeSerializableValue(deserializer)
  }

  /**
   * Decode a value from a byte array according to the BCS specification.
   *
   * This is an inline overload convenience method to avoid the need to explicitly pass a deserializer.
   *
   * @param bytes The byte array to decode.
   * @return The decoded value.
   */
  inline fun <reified T> decodeFromByteArray(bytes: ByteArray): T =
    decodeFromByteArray(bytes, serializer())

}
