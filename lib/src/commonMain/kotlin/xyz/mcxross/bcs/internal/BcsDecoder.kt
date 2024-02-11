package xyz.mcxross.bcs.internal

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.AbstractDecoder
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import xyz.mcxross.bcs.MAX_CONTAINER_DEPTH
import xyz.mcxross.bcs.exception.ExceededMaxLen
import xyz.mcxross.bcs.exception.NotSupported
import xyz.mcxross.bcs.stream.BcsDataInputBuffer

@OptIn(ExperimentalSerializationApi::class)
class BcsDecoder(
  private val depth: UInt = 0u,
  private val inputBuffer: BcsDataInputBuffer,
  private var elementsCount: Int = 0,
) : AbstractDecoder() {
  private var elementIndex = 0

  override val serializersModule: SerializersModule = EmptySerializersModule()

  override fun decodeBoolean(): Boolean = inputBuffer.readBoolean()

  override fun decodeByte(): Byte = inputBuffer.readByte()

  override fun decodeShort(): Short = inputBuffer.readShort()

  override fun decodeInt(): Int = inputBuffer.readInt()

  override fun decodeLong(): Long = inputBuffer.readLong()

  override fun decodeFloat(): Float = throw NotSupported("Not supported: serialize Float")

  override fun decodeDouble(): Double = throw NotSupported("Not supported: serialize Double")

  override fun decodeChar(): Char = throw NotSupported("Not supported: serialize Char")

  override fun decodeString(): String = inputBuffer.readUTF()

  override fun decodeEnum(enumDescriptor: SerialDescriptor): Int {
    return inputBuffer.readULEB128()
  }

  override fun decodeNull(): Nothing? {
    return null
  }

  override fun decodeNotNullMark(): Boolean = inputBuffer.readShort().toInt() == 1

  @Suppress("UNCHECKED_CAST")
  override fun <T> decodeSerializableValue(
    deserializer: DeserializationStrategy<T>,
    previousValue: T?,
  ): T {
    val unitDescriptor = serialDescriptor<Unit>()
    return if (deserializer.descriptor == unitDescriptor) decodeUnit() as T
    else super.decodeSerializableValue(deserializer, previousValue)
  }

  private fun decodeUnit(): Int {
    return inputBuffer.readULEB128()
  }

  override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
    if (elementIndex == elementsCount) return CompositeDecoder.DECODE_DONE
    return elementIndex++
  }

  override fun decodeSequentially(): Boolean = true

  override fun decodeCollectionSize(descriptor: SerialDescriptor): Int {
    return inputBuffer.peek().toInt().also {
      inputBuffer.skip(1)
      elementsCount = it
    }
  }

  override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
    if (depth > MAX_CONTAINER_DEPTH) {
      throw ExceededMaxLen("Recursion depth limit exceeded")
    }
    return BcsDecoder(depth + 1u, inputBuffer, descriptor.elementsCount)
  }
}
