package xyz.mcxross.bcs

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import xyz.mcxross.bcs.exception.NotSupported

class Serde {

  private val bcs = Bcs {}

  @Test
  fun testBoolean() {
    val encoding = bcs.encodeToByteArray(true)
    assertContentEquals(byteArrayOf(1.toByte()), encoding)
  }

  @Test
  fun testByte() {
    val encoding = bcs.encodeToByteArray(1.toByte())
    assertContentEquals(byteArrayOf(1.toByte()), encoding)
  }

  @Test
  fun testUTF() {
    val encoding = bcs.encodeToByteArray("çå∞≠¢õß∂ƒ∫")
    val expected =
      byteArrayOf(
        24.toByte(),
        0xc3.toByte(),
        0xa7.toByte(),
        0xc3.toByte(),
        0xa5.toByte(),
        0xe2.toByte(),
        0x88.toByte(),
        0x9e.toByte(),
        0xe2.toByte(),
        0x89.toByte(),
        0xa0.toByte(),
        0xc2.toByte(),
        0xa2.toByte(),
        0xc3.toByte(),
        0xb5.toByte(),
        0xc3.toByte(),
        0x9f.toByte(),
        0xe2.toByte(),
        0x88.toByte(),
        0x82.toByte(),
        0xc6.toByte(),
        0x92.toByte(),
        0xe2.toByte(),
        0x88.toByte(),
        0xab.toByte(),
      )
    assertContentEquals(expected, encoding)
  }

  @Test
  fun testMapUTF() {

    val map =
      mapOf(
        "ASCII only" to "Hello, World!",
        "Latin diacritics" to "çåéüñß",
        "Mathematical symbols" to "∞≠±∑∫√",
        "Currency symbols" to "€£¥¢$₹₽₿",
        "Emojis" to "😀🚀🌍🔥🎉",
        "Mixed characters" to "çå∞≠¢õß∂ƒ∫",
        "CJK characters" to "你好世界こんにちは안녕하세요",
        "Right-to-left" to "مرحبا שלום",
        "Empty string" to "",
      )

    val encoding = bcs.encodeToByteArray(map)
    val decoded = bcs.decodeFromByteArray<Map<String, String>>(encoding)

    assertEquals(decoded, map)
  }

  @Test
  fun testFloat() {
    assertFailsWith<NotSupported>("Floats type not supported") {
      val encoding = bcs.encodeToByteArray<Float>(3.14F)
      bcs.decodeFromByteArray<Float>(encoding)
    }
  }

  @Test
  fun testDouble() {
    assertFailsWith<NotSupported>("Double type are not supported") {
      val encoding = bcs.encodeToByteArray<Double>(3.14)
      bcs.decodeFromByteArray<Double>(encoding)
    }
  }

  @Test
  fun testChar() {
    assertFailsWith<NotSupported>("Char type not supported") {
      val encoding = bcs.encodeToByteArray<Char>('a')
      bcs.decodeFromByteArray<Char>(encoding)
    }
  }

  @Test
  fun canDecodeVectorOfLength128() {
    val base = ByteArray(128) { it.toByte() }
    val expectedLengthTag = listOf(0x80.toByte(), 0x01.toByte()).toByteArray()
    val encoded = expectedLengthTag + base
    val decoded = bcs.decodeFromByteArray<List<Byte>>(encoded)

    val expected = base.toList()
    assertContentEquals(expected, decoded)
  }
}
