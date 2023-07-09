package xyz.mcxross.bcs.internal

import kotlinx.serialization.encoding.AbstractEncoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

class BcsEncoder() : AbstractEncoder() {

  override val serializersModule: SerializersModule = EmptySerializersModule()
  override fun encodeBoolean(value: Boolean) {
    //TODO Implement
  }

}
