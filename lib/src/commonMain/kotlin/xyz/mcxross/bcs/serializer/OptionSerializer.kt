package xyz.mcxross.bcs.serializer

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PolymorphicKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import xyz.mcxross.bcs.model.Option

class OptionSerializer<T>(private val dataSerializer: KSerializer<T>) : KSerializer<Option<T>> {
  @OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
  override val descriptor: SerialDescriptor =
    buildSerialDescriptor("Response", PolymorphicKind.SEALED) {
      element("Ok", buildClassSerialDescriptor("Ok") { element<String>("message") })
      element("Error", dataSerializer.descriptor)
    }

  override fun deserialize(decoder: Decoder): Option<T> {
    TODO("Not yet implemented")
  }

  override fun serialize(encoder: Encoder, value: Option<T>) {
    TODO("Not yet implemented")
  }
}
