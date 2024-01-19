package xyz.mcxross.bcs.stream

import xyz.mcxross.bcs.ext.toBoolean

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
    (0 ..< next).forEach { result[it] = requireNextByte() }
    return result
  }

  override fun toByteArray() = byteArray

  fun readByte(): Byte {
    return requireNextByte()
  }

  fun readULEB128(): Int {
    var result = 0
    var shift = 0
    var byte: Int
    do {
      byte = requireNextByte().toInt()
      result = result or ((byte and 0x7F) shl shift)
      shift += 7
    } while (byte and 0x80 != 0)
    return result
  }

  fun readBoolean(): Boolean = readByte().toBoolean()

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

  fun readUTF(): String = takeNext(readULEB128()).decodeToString()
}
