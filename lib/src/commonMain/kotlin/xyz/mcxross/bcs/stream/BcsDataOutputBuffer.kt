package xyz.mcxross.bcs.stream

import xyz.mcxross.bcs.ext.toByte

internal class BcsDataOutputBuffer(initialCapacity: Int = 32) : BcsDataBuffer {

  private var buffer = ByteArray(initialCapacity)
  private var position = 0

  private fun ensureCapacity(additionalBytes: Int) {
    if (position + additionalBytes > buffer.size) {
      val newSize = maxOf(buffer.size * 2, position + additionalBytes)
      buffer = buffer.copyOf(newSize)
    }
  }

  fun add(byte: Byte) {
    ensureCapacity(1)
    buffer[position++] = byte
  }

  fun addAll(bytes: ByteArray) {
    ensureCapacity(bytes.size)
    bytes.copyInto(buffer, position)
    position += bytes.size
  }

  fun addAll(bytes: List<Byte>) {
    ensureCapacity(bytes.size)
    for (i in bytes.indices) {
      buffer[position + i] = bytes[i]
    }
    position += bytes.size
  }

  override fun toByteArray(): ByteArray = buffer.copyOf(position)

  fun writeBoolean(booleanValue: Boolean) {
    add(booleanValue.toByte())
  }

  fun writeByte(byteValue: Byte) {
    add(byteValue)
  }

  fun writeULEB128(value: Int) {
    var v = value
    while (v > 0x7F) {
      add((v and 0x7F or 0x80).toByte())
      v = v shr 7
    }
    add(v.toByte())
  }

  fun writeShort(shortValue: Short) {
    ensureCapacity(2)

    buffer[position++] = shortValue.toByte()
    buffer[position++] = (shortValue.toInt() shr 8).toByte()
  }

  fun writeInt(intValue: Int) {
    ensureCapacity(4)

    buffer[position++] = intValue.toByte()
    buffer[position++] = (intValue shr 8).toByte()
    buffer[position++] = (intValue shr 16).toByte()
    buffer[position++] = (intValue shr 24).toByte()
  }

  fun writeLong(longValue: Long) {
    ensureCapacity(8)

    buffer[position++] = longValue.toByte()
    buffer[position++] = (longValue shr 8).toByte()
    buffer[position++] = (longValue shr 16).toByte()
    buffer[position++] = (longValue shr 24).toByte()
    buffer[position++] = (longValue shr 32).toByte()
    buffer[position++] = (longValue shr 40).toByte()
    buffer[position++] = (longValue shr 48).toByte()
    buffer[position++] = (longValue shr 56).toByte()
  }

  fun writeUTF(text: String) {
    val utfBytes = text.encodeToByteArray()
    writeULEB128(utfBytes.size)
    addAll(utfBytes)
  }

  fun clear() {
    position = 0
  }

  fun size(): Int = position
}
