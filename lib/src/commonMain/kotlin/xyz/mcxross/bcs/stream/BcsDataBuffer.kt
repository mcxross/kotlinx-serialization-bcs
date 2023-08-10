package xyz.mcxross.bcs.stream

import xyz.mcxross.bcs.ext.toBoolean
import xyz.mcxross.bcs.ext.toByte

interface BcsDataBuffer {
  fun toByteArray(): ByteArray
}

class BcsDataOutputBuffer : BcsDataBuffer {

  val bytes = mutableListOf<Byte>()

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

  fun writeShort(shortValue: Short) = repeat(2) {
    bytes.add((shortValue.toInt() shr (8 - it * 8)).toByte())
  }

  fun writeInt(intValue: Int) = repeat(4) {
    bytes.add((intValue shr (24 - it * 8)).toByte())
  }

  fun writeLong(longValue: Long) = repeat(8) {
    bytes.add((longValue shr (56 - it * 8)).toByte())
  }

  fun writeDouble(doubleValue: Double) = writeLong(doubleValue.toRawBits())

  fun writeUTF(text: String) {
    val utfBytes = text.encodeToByteArray()
    val size = utfBytes.size
    if (size <= Short.MAX_VALUE) {
      writeShort(size.toShort())
    } else {
      writeInt(size)
    }
    bytes.addAll(utfBytes.toList())
  }
}

class BcsDataInputBuffer(private val byteArray: ByteArray) : BcsDataBuffer {
  private var currentIndex = 0

  fun skip(bytes: Int) {
    currentIndex += bytes
  }

  fun peek(): Byte = byteArray.getOrNull(currentIndex) ?: throw Exception("End of stream")
  fun peekSafely(): Byte? = byteArray.getOrNull(currentIndex)

  // Increases index only if next byte is not null
  fun nextByteOrNull(): Byte? = byteArray.getOrNull(currentIndex)?.also { currentIndex++ }

  fun requireNextByte(): Byte = nextByteOrNull() ?: throw Exception("End of stream")

  fun takeNext(next: Int): ByteArray {
    require(next > 0) { "Number of bytes to take must be greater than 0!" }
    val result = ByteArray(next)
    (0..<next).forEach {
      result[it] = requireNextByte()
    }
    return result
  }

  override fun toByteArray() = byteArray

  fun readByte(): Byte {
    return peek()
  }

  fun readBoolean(): Boolean = peek().toBoolean()

  fun readShort(): Short {
    val bytes = takeNext(2)
    return (bytes[0].toInt() shl 8 or (bytes[1].toInt() and 0xFF)).toShort()
  }

  fun readInt(): Int {
    val bytes = takeNext(4)
    return (bytes[0].toInt() shl 24) or
      (bytes[1].toInt() shl 16) or
      (bytes[2].toInt() shl 8) or
      (bytes[3].toInt() and 0xFF)
  }

  fun readLong(): Long {
    val bytes = takeNext(8)
    return (bytes[0].toLong() shl 56) or
      (bytes[1].toLong() shl 48) or
      (bytes[2].toLong() shl 40) or
      (bytes[3].toLong() shl 32) or
      (bytes[4].toLong() shl 24) or
      (bytes[5].toLong() shl 16) or
      (bytes[6].toLong() shl 8) or
      (bytes[7].toLong() and 0xFF)
  }

  fun readDouble(): Double {
    val bits: Long = readLong()
    // TODO doesn't work for all, fix.
    return Double.fromBits(bits)
  }

  fun readUTF(): String {
    val length = readShort().toInt()
    return takeNext(length).decodeToString()
  }
}

internal fun ByteArray.toBcsBuffer() = BcsDataInputBuffer(this)
