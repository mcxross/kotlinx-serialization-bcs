package xyz.mcxross.bcs.serializer

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.nullable
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import xyz.mcxross.bcs.model.Option

/** Round-tripping a `Some` that wraps `null` yields [Option.None]. */
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
