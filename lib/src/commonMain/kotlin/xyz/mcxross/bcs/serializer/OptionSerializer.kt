package xyz.mcxross.bcs.serializer

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.nullable
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import xyz.mcxross.bcs.model.Option

/**
 * Serializer for [Option].
 *
 * BCS encodes an optional value as a single tag byte — `0x00` for absent, `0x01` for present —
 * followed by the value itself when present. Rather than writing that tag directly, this delegates
 * to the encoder's null handling, so `Option<T>` and a plain nullable `T?` always encode to the
 * same bytes.
 *
 * Note that a `Some` wrapping `null` is indistinguishable from `None` on the wire, so
 * round-tripping `Some(null)` yields `None`.
 */
class OptionSerializer<T>(private val dataSerializer: KSerializer<T>) : KSerializer<Option<T>> {

  override val descriptor: SerialDescriptor = dataSerializer.descriptor.nullable

  @OptIn(ExperimentalSerializationApi::class)
  override fun serialize(encoder: Encoder, value: Option<T>) {
    when (value) {
      is Option.None -> encoder.encodeNull()
      is Option.Some -> {
        encoder.encodeNotNullMark()
        encoder.encodeSerializableValue(dataSerializer, value.value)
      }
    }
  }

  override fun deserialize(decoder: Decoder): Option<T> =
    if (decoder.decodeNotNullMark()) Option.Some(decoder.decodeSerializableValue(dataSerializer))
    else {
      decoder.decodeNull()
      Option.None
    }
}
