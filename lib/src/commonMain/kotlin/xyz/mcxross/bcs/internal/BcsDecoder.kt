package xyz.mcxross.bcs.internal

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.AbstractDecoder
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import xyz.mcxross.bcs.exception.NotSupported
import xyz.mcxross.bcs.stream.BcsDataInputBuffer

@OptIn(ExperimentalSerializationApi::class)
class BcsDecoder(private val inputBuffer: BcsDataInputBuffer) : AbstractDecoder() {
  private var elementIndex = 0

  override val serializersModule: SerializersModule = EmptySerializersModule()
  override fun decodeBoolean(): Boolean = inputBuffer.readBoolean()
  override fun decodeByte(): Byte = inputBuffer.readByte()
  override fun decodeShort(): Short = inputBuffer.readShort()
  override fun decodeInt(): Int = inputBuffer.readInt()
  override fun decodeLong(): Long = inputBuffer.readLong()
  override fun decodeFloat(): Float = throw NotSupported("Not supported: serialize Float")
  override fun decodeDouble(): Double = inputBuffer.readDouble()
  override fun decodeChar(): Char = throw NotSupported("Not supported: serialize Char")
  override fun decodeString(): String = inputBuffer.readUTF()
  override fun decodeEnum(enumDescriptor: SerialDescriptor): Int {
    //TODO Implement
    return 1
  }

  override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
    if (elementIndex == descriptor.elementsCount) return CompositeDecoder.DECODE_DONE
    return elementIndex++
  }

  override fun decodeSequentially(): Boolean = true

  override fun decodeCollectionSize(descriptor: SerialDescriptor): Int =
    decodeInt().also { }

  override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder =
    BcsDecoder(inputBuffer)

}
