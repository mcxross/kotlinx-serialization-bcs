package xyz.mcxross.bcs.stream

interface BcsDataBuffer {
  fun toByteArray(): ByteArray
}

class BcsDataOutputBuffer() : BcsDataBuffer {

  private val bytes = mutableListOf<Byte>()

  fun add(byte: Byte) = bytes.add(byte)
  fun addAll(bytes: List<Byte>) = this.bytes.addAll(bytes)
  fun addAll(bytes: ByteArray) = this.bytes.addAll(bytes.toList())

  override fun toByteArray() = bytes.toByteArray()
}

class BcsDataInputBuffer(private val byteArray: ByteArray) : BcsDataBuffer {
  var index = 0
    private set

  fun skip(bytes: Int) {
    index += bytes
  }

  fun peek(): Byte = byteArray.getOrNull(index) ?: throw Exception("End of stream")
  fun peekSafely(): Byte? = byteArray.getOrNull(index)

  // Increases index only if next byte is not null
  fun nextByteOrNull(): Byte? = byteArray.getOrNull(index)?.also { index++ }

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
}

internal fun ByteArray.toBcsBuffer() = BcsDataInputBuffer(this)
