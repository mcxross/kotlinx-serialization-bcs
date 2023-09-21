package xyz.mcxross.bcs.serializer

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PolymorphicKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.descriptors.element
import xyz.mcxross.bcs.model.Option

abstract class OptionSerializer<T>(private val dataSerializer: KSerializer<T>) : KSerializer<Option<T>> {
  @OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
  override val descriptor: SerialDescriptor =
    buildSerialDescriptor("Response", PolymorphicKind.SEALED) {
      element("Ok", buildClassSerialDescriptor("Ok") { element<String>("message") })
      element("Error", dataSerializer.descriptor)
    }
}
