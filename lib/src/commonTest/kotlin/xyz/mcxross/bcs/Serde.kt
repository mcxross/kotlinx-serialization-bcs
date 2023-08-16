package xyz.mcxross.bcs

import kotlin.test.Test
import kotlin.test.assertContentEquals

class Serde {

  private val bcs = Bcs {}

  @Test
  fun testUTF() {
    val encoding = bcs.encodeToBinary("çå∞≠¢õß∂ƒ∫")
    val expected = byteArrayOf(
      24.toByte(),
      0xc3.toByte(), 0xa7.toByte(), 0xc3.toByte(), 0xa5.toByte(),
      0xe2.toByte(), 0x88.toByte(), 0x9e.toByte(), 0xe2.toByte(),
      0x89.toByte(), 0xa0.toByte(), 0xc2.toByte(), 0xa2.toByte(),
      0xc3.toByte(), 0xb5.toByte(), 0xc3.toByte(), 0x9f.toByte(),
      0xe2.toByte(), 0x88.toByte(), 0x82.toByte(), 0xc6.toByte(),
      0x92.toByte(), 0xe2.toByte(), 0x88.toByte(), 0xab.toByte(),
    )
    assertContentEquals(expected, encoding)
  }

}
