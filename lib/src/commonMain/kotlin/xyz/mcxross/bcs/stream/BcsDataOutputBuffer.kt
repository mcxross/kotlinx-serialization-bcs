package xyz.mcxross.bcs.stream

import xyz.mcxross.bcs.ext.toByte

class BcsDataOutputBuffer : BcsDataBuffer {

  private val bytes = mutableListOf<Byte>()

  fun add(byte: Byte) = bytes.add(byte)

  fun addAll(bytes: List<Byte>) = this.bytes.addAll(bytes)

  fun addAll(bytes: ByteArray) = this.bytes.addAll(bytes.toList())

  override fun toByteArray() = bytes.toByteArray()

  fun writeBoolean(booleanValue: Boolean) {
    bytes.add(booleanValue.toByte())
  }

  fun writeByte(byteValue: Byte) {
    bytes.add(byteValue)
  }

  fun writeULEB128(value: Int) {
    var v = value
    while (v > 0x7F) {
      bytes.add((v and 0x7F or 0x80).toByte())
      v = v shr 7
    }
    bytes.add(v.toByte())
  }

  fun writeShort(shortValue: Short) =
    repeat(2) { bytes.add((shortValue.toInt() shr (8 - it * 8)).toByte()) }

  fun writeInt(intValue: Int) = repeat(4) { bytes.add((intValue shr (24 - it * 8)).toByte()) }

  fun writeLong(longValue: Long) = repeat(8) { bytes.add((longValue shr (56 - it * 8)).toByte()) }

  fun writeDouble(doubleValue: Double) = writeLong(doubleValue.toRawBits())

  fun writeUTF(text: String) {
    val utfBytes = text.encodeToByteArray()
    writeULEB128(utfBytes.size)
    bytes.addAll(utfBytes.toList())
  }
}
