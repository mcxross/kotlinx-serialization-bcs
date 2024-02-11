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
    return ((bytes[1].toInt() shl 8) or (bytes[0].toInt() and 0xFF)).toShort()
  }

  fun readInt(): Int {
    val bytes = takeNext(4)
    return bytes.foldIndexed(0) { index, acc, byte ->
      acc or ((byte.toInt() and 0xFF) shl (index * 8))
    }
  }

  fun readLong(): Long {
    val bytes = takeNext(8)
    return bytes.indices.fold(0L) { acc, i -> acc or ((bytes[i].toLong() and 0xFF) shl (i * 8)) }
  }

  fun readUTF(): String = takeNext(readULEB128()).decodeToString()
}
