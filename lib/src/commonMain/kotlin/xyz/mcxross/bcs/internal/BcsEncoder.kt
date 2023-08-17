package xyz.mcxross.bcs.internal

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.AbstractEncoder
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import xyz.mcxross.bcs.MAX_SEQUENCE_LENGTH
import xyz.mcxross.bcs.exception.ExceededMaxLen
import xyz.mcxross.bcs.exception.NotSupported
import xyz.mcxross.bcs.stream.BcsDataOutputBuffer
import kotlin.experimental.or

@OptIn(ExperimentalSerializationApi::class)
class BcsEncoder : AbstractEncoder() {

  val output = BcsDataOutputBuffer()

  override val serializersModule: SerializersModule = EmptySerializersModule()

  private fun outputU32AsUleb128(value: Int) {
    var mutableValue = value
    while (mutableValue >= 0x80) {
      val byte: Byte = (mutableValue and 0x7F).toByte()
      output.add(byte or 0x80.toByte())
      mutableValue = mutableValue shr 7
    }
    output.add(mutableValue.toByte())
  }

  private fun outputSeqLen(len: UInt) {
    if (len > MAX_SEQUENCE_LENGTH) {
      throw ExceededMaxLen("Exceeded max sequence length: $len")
    }
    this.outputU32AsUleb128(len.toInt())
  }

  override fun encodeBoolean(value: Boolean) = output.writeBoolean(value)

  override fun encodeByte(value: Byte) = output.writeByte(value)

  override fun encodeShort(value: Short) = output.writeShort(value)

  override fun encodeInt(value: Int) = output.writeInt(value)

  override fun encodeLong(value: Long) = output.writeLong(value)

  override fun encodeDouble(value: Double) = output.writeDouble(value)

  override fun encodeString(value: String) = output.writeUTF(value)

  override fun beginCollection(
    descriptor: SerialDescriptor,
    collectionSize: Int
  ): CompositeEncoder {
    outputSeqLen(collectionSize.toUInt())
    return this
  }

  override fun encodeChar(value: Char) = throw NotSupported("Not supported: serialize Char")

  override fun encodeFloat(value: Float) = throw NotSupported("Not supported: serialize Float")

  override fun encodeNull() {
    encodeShort(0)
  }

  override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) {
    outputU32AsUleb128(index)
  }

  override fun <T> encodeSerializableValue(serializer: SerializationStrategy<T>, value: T) {
    val unitSerializer = serializer<Unit>()
    if (serializer.descriptor == unitSerializer.descriptor)
      encodeUnit(value as Unit)
    else
      super.encodeSerializableValue(serializer, value)

  }

  private fun encodeUnit(unit: Unit) {}
}
