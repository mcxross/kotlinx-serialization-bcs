package xyz.mcxross.bcs

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import xyz.mcxross.bcs.stream.BcsDataInputBuffer
import xyz.mcxross.bcs.stream.BcsDataOutputBuffer

/**
 * Covers the stream buffers directly: ULEB128 continuation handling, output buffer capacity growth,
 * and the bounds checks that guard decoding of untrusted input.
 */
class StreamBuffer {

  // ---------- ULEB128 ----------

  @Test
  fun ulebRoundTripsAcrossContinuationBoundaries() {
    val values =
      listOf(
        0,
        1,
        0x7F,
        0x80,
        0xFF,
        0x100,
        16383,
        16384,
        2097151,
        2097152,
        268435455,
        Int.MAX_VALUE,
      )
    for (v in values) {
      val out = BcsDataOutputBuffer()
      out.writeULEB128(v)
      assertEquals(
        v,
        BcsDataInputBuffer(out.toByteArray()).readULEB128(),
        "round-trip failed for $v",
      )
    }
  }

  @Test
  fun ulebEncodesKnownMultiByteSequences() {
    // 128 is the smallest value that needs a continuation byte.
    val one = BcsDataOutputBuffer()
    one.writeULEB128(128)
    assertContentEquals(byteArrayOf(0x80.toByte(), 0x01), one.toByteArray())

    val two = BcsDataOutputBuffer()
    two.writeULEB128(16384)
    assertContentEquals(byteArrayOf(0x80.toByte(), 0x80.toByte(), 0x01), two.toByteArray())
  }

  @Test
  fun ulebDecodesContinuationBytesAboveSignedByteRange() {
    // Every continuation byte is >= 0x80, i.e. negative as a signed Byte.
    assertEquals(
      268435455,
      BcsDataInputBuffer(byteArrayOf(0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0x7F))
        .readULEB128(),
    )
  }

  @Test
  fun ulebThrowsOnTruncatedSequence() {
    val e =
      assertFailsWith<Exception> { BcsDataInputBuffer(byteArrayOf(0x80.toByte())).readULEB128() }
    assertEquals("Unexpected end of ULEB128 sequence", e.message)
  }

  // ---------- output buffer capacity growth ----------

  @Test
  fun growsPastDefaultInitialCapacity() {
    val out = BcsDataOutputBuffer()
    val payload = ByteArray(200) { (it % 256).toByte() }
    payload.forEach { out.add(it) }
    assertEquals(200, out.size())
    assertContentEquals(payload, out.toByteArray())
  }

  @Test
  fun growsWhenSingleWriteExceedsDoubling() {
    // 1000 bytes into a 4-byte buffer: doubling alone is not enough, so the
    // position + additionalBytes branch of ensureCapacity has to win.
    val out = BcsDataOutputBuffer(initialCapacity = 4)
    val big = ByteArray(1000) { (it % 251).toByte() }
    out.addAll(big)
    assertContentEquals(big, out.toByteArray())
  }

  @Test
  fun growsFromSmallestUsefulCapacity() {
    val out = BcsDataOutputBuffer(initialCapacity = 1)
    repeat(50) { out.add(it.toByte()) }
    assertEquals(50, out.size())
    assertContentEquals(ByteArray(50) { it.toByte() }, out.toByteArray())
  }

  @Test
  fun multiByteWriteStraddlingCapacityKeepsAllBytes() {
    // Leaves one free byte, then writes eight.
    val out = BcsDataOutputBuffer(initialCapacity = 8)
    repeat(7) { out.add(0x11) }
    out.writeLong(0x0102030405060708L)
    val bytes = out.toByteArray()
    assertEquals(15, bytes.size)
    assertEquals(0x08, bytes[7]) // little endian: low byte first
    assertEquals(0x01, bytes[14])
    assertEquals(0x0102030405060708L, BcsDataInputBuffer(bytes).also { it.skip(7) }.readLong())
  }

  @Test
  fun addAllListAndByteArrayAgree() {
    val bytes = ByteArray(100) { (it * 7 % 256).toByte() }
    val viaArray = BcsDataOutputBuffer(initialCapacity = 1).also { it.addAll(bytes) }
    val viaList = BcsDataOutputBuffer(initialCapacity = 1).also { it.addAll(bytes.toList()) }
    assertContentEquals(viaArray.toByteArray(), viaList.toByteArray())
  }

  @Test
  fun clearResetsLengthAndAllowsReuse() {
    val out = BcsDataOutputBuffer()
    out.writeInt(42)
    out.clear()
    assertEquals(0, out.size())
    out.writeInt(7)
    assertEquals(4, out.size())
    assertEquals(7, BcsDataInputBuffer(out.toByteArray()).readInt())
  }

  // ---------- input buffer bounds checks ----------

  @Test
  fun skipToExactEndIsAllowedButBeyondThrows() {
    BcsDataInputBuffer(byteArrayOf(1, 2, 3)).skip(3)
    val e = assertFailsWith<Exception> { BcsDataInputBuffer(byteArrayOf(1, 2, 3)).skip(4) }
    assertEquals("Cannot skip beyond end of buffer", e.message)
  }

  @Test
  fun peekAndNextAtEndOfStream() {
    val empty = BcsDataInputBuffer(ByteArray(0))
    assertNull(empty.peekSafely())
    assertNull(empty.nextByteOrNull())
    assertEquals("End of stream", assertFailsWith<Exception> { empty.peek() }.message)
    assertEquals("End of stream", assertFailsWith<Exception> { empty.requireNextByte() }.message)
  }

  @Test
  fun takeNextRejectsNonPositiveAndOverlongReads() {
    assertFailsWith<IllegalArgumentException> { BcsDataInputBuffer(byteArrayOf(1)).takeNext(0) }
    assertFailsWith<IllegalArgumentException> { BcsDataInputBuffer(byteArrayOf(1)).takeNext(-1) }
    val e = assertFailsWith<Exception> { BcsDataInputBuffer(byteArrayOf(1, 2)).takeNext(3) }
    assertEquals("Not enough bytes in buffer", e.message)
  }

  @Test
  fun fixedWidthReadsRejectTruncatedInput() {
    assertEquals(
      "Not enough bytes to read Short",
      assertFailsWith<Exception> { BcsDataInputBuffer(byteArrayOf(1)).readShort() }.message,
    )
    assertEquals(
      "Not enough bytes to read Int",
      assertFailsWith<Exception> { BcsDataInputBuffer(byteArrayOf(1, 2, 3)).readInt() }.message,
    )
    assertEquals(
      "Not enough bytes to read Long",
      assertFailsWith<Exception> { BcsDataInputBuffer(ByteArray(7)).readLong() }.message,
    )
  }

  @Test
  fun readUTFRejectsLengthLongerThanBuffer() {
    // Length prefix claims 10 bytes, only 2 follow.
    val e = assertFailsWith<Exception> { BcsDataInputBuffer(byteArrayOf(10, 0x61, 0x62)).readUTF() }
    assertEquals("Not enough bytes to read UTF string of length 10", e.message)
  }

  @Test
  fun readUTFRejectsLengthPrefixThatOverflowsPositionPlusLength() {
    // position + length wraps negative for a hostile length, so the guard has to be
    // written as length > size - position to fire at all.
    val out = BcsDataOutputBuffer()
    out.writeULEB128(Int.MAX_VALUE)
    out.addAll(byteArrayOf(0x61, 0x62))
    val e = assertFailsWith<Exception> { BcsDataInputBuffer(out.toByteArray()).readUTF() }
    assertEquals("Not enough bytes to read UTF string of length 2147483647", e.message)
  }

  @Test
  fun readUTFRejectsLengthPrefixDecodingToNegative() {
    // A 5-byte ULEB128 sequence can decode past Int range and come out negative.
    val hostile = byteArrayOf(0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0x0F)
    val buffer = BcsDataInputBuffer(hostile + byteArrayOf(0x61, 0x62))
    val e = assertFailsWith<Exception> { buffer.readUTF() }
    assertEquals("Not enough bytes to read UTF string of length -1", e.message)
  }

  @Test
  fun skipRejectsNegativeCount() {
    // A negative skip would otherwise rewind the cursor.
    val buffer = BcsDataInputBuffer(byteArrayOf(1, 2, 3))
    buffer.skip(2)
    assertFailsWith<Exception> { buffer.skip(-2) }
    assertEquals(2, buffer.position())
  }

  @Test
  fun positionAndRemainingTrackReads() {
    val buffer = BcsDataInputBuffer(byteArrayOf(1, 2, 3, 4))
    assertEquals(0, buffer.position())
    assertEquals(4, buffer.remaining())
    assertTrue(buffer.hasRemaining())

    buffer.skip(4)
    assertEquals(4, buffer.position())
    assertEquals(0, buffer.remaining())
    assertFalse(buffer.hasRemaining())

    buffer.reset()
    assertEquals(0, buffer.position())
    assertTrue(buffer.hasRemaining())
  }

  @Test
  fun fixedWidthReadsTreatBytesAsUnsigned() {
    // All-ones must decode to -1 rather than being corrupted by sign extension.
    assertEquals(-1, BcsDataInputBuffer(ByteArray(2) { 0xFF.toByte() }).readShort().toInt())
    assertEquals(-1, BcsDataInputBuffer(ByteArray(4) { 0xFF.toByte() }).readInt())
    assertEquals(-1L, BcsDataInputBuffer(ByteArray(8) { 0xFF.toByte() }).readLong())
  }

  // ---------- end to end through the public API ----------

  @Test
  fun encodesAndDecodesCollectionsAtUlebLengthBoundaries() {
    val bcs = Bcs {}
    for (size in listOf(0, 1, 127, 128, 129, 16383, 16384)) {
      val value = List(size) { (it % 256).toByte() }
      val decoded = bcs.decodeFromByteArray<List<Byte>>(bcs.encodeToByteArray(value))
      assertContentEquals(value, decoded, "collection of size $size did not round-trip")
    }
  }
}
