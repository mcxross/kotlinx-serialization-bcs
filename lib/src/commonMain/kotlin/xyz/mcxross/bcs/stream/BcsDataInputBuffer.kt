package xyz.mcxross.bcs.stream

import xyz.mcxross.bcs.ext.toBoolean

class BcsDataInputBuffer(private val byteArray: ByteArray) : BcsDataBuffer {
  private var position = 0

  fun skip(bytes: Int) {
    if (bytes < 0 || bytes > byteArray.size - position) {
      throw Exception("Cannot skip beyond end of buffer")
    }
    position += bytes
  }

  fun peek(): Byte {
    if (position >= byteArray.size) {
      throw Exception("End of stream")
    }
    return byteArray[position]
  }

  fun peekSafely(): Byte? = if (position < byteArray.size) byteArray[position] else null

  fun nextByteOrNull(): Byte? = if (position < byteArray.size) byteArray[position++] else null

  fun requireNextByte(): Byte =
    if (position < byteArray.size) byteArray[position++] else throw Exception("End of stream")

  fun takeNext(next: Int): ByteArray {
    require(next > 0) { "Number of bytes to take must be greater than 0!" }

    if (next > byteArray.size - position) {
      throw Exception("Not enough bytes in buffer")
    }

    val result = ByteArray(next)
    byteArray.copyInto(result, 0, position, position + next)
    position += next
    return result
  }

  override fun toByteArray() = byteArray

  fun readByte(): Byte = requireNextByte()

  fun readULEB128(): Int {
    var result = 0
    var shift = 0
    var byte: Int

    do {
      if (position >= byteArray.size) {
        throw Exception("Unexpected end of ULEB128 sequence")
      }

      byte = byteArray[position++].toInt() and 0xFF
      result = result or ((byte and 0x7F) shl shift)
      shift += 7
    } while (byte and 0x80 != 0)

    return result
  }

  fun readBoolean(): Boolean = requireNextByte().toBoolean()

  fun readShort(): Short {
    if (byteArray.size - position < 2) {
      throw Exception("Not enough bytes to read Short")
    }

    val b0 = byteArray[position++].toInt() and 0xFF
    val b1 = byteArray[position++].toInt() and 0xFF

    return ((b1 shl 8) or b0).toShort()
  }

  fun readInt(): Int {
    if (byteArray.size - position < 4) {
      throw Exception("Not enough bytes to read Int")
    }

    val b0 = byteArray[position++].toInt() and 0xFF
    val b1 = byteArray[position++].toInt() and 0xFF
    val b2 = byteArray[position++].toInt() and 0xFF
    val b3 = byteArray[position++].toInt() and 0xFF

    return b0 or (b1 shl 8) or (b2 shl 16) or (b3 shl 24)
  }

  fun readLong(): Long {
    if (byteArray.size - position < 8) {
      throw Exception("Not enough bytes to read Long")
    }

    val b0 = byteArray[position++].toLong() and 0xFF
    val b1 = byteArray[position++].toLong() and 0xFF
    val b2 = byteArray[position++].toLong() and 0xFF
    val b3 = byteArray[position++].toLong() and 0xFF
    val b4 = byteArray[position++].toLong() and 0xFF
    val b5 = byteArray[position++].toLong() and 0xFF
    val b6 = byteArray[position++].toLong() and 0xFF
    val b7 = byteArray[position++].toLong() and 0xFF

    return b0 or
      (b1 shl 8) or
      (b2 shl 16) or
      (b3 shl 24) or
      (b4 shl 32) or
      (b5 shl 40) or
      (b6 shl 48) or
      (b7 shl 56)
  }

  fun readUTF(): String {
    val length = readULEB128()

    if (length < 0 || length > byteArray.size - position) {
      throw Exception("Not enough bytes to read UTF string of length $length")
    }

    val result = byteArray.decodeToString(position, position + length)
    position += length
    return result
  }

  fun hasRemaining(): Boolean = position < byteArray.size

  fun remaining(): Int = byteArray.size - position

  fun position(): Int = position

  fun reset() {
    position = 0
  }
}
