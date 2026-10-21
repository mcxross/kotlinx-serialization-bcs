package xyz.mcxross.bcs

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlinx.serialization.Serializable
import xyz.mcxross.bcs.exception.NotSupported
import xyz.mcxross.bcs.model.Option

@Serializable private data class WithNullable(val id: Int, val label: String?)

@Serializable private data class WithOption(val id: Int, val label: Option<String>)

class OptionAndNull {

  private val bcs = Bcs {}

  @Test
  fun nullIsASingleZeroByte() {
    assertContentEquals(byteArrayOf(0), bcs.encodeToByteArray<Int?>(null))
  }

  @Test
  fun presentValueIsTaggedWithASingleOneByte() {
    assertContentEquals(byteArrayOf(1, 42, 0, 0, 0), bcs.encodeToByteArray<Int?>(42))
  }

  @Test
  fun nullableRoundTrips() {
    assertEquals(null, bcs.decodeFromByteArray<Int?>(bcs.encodeToByteArray<Int?>(null)))
    assertEquals(42, bcs.decodeFromByteArray<Int?>(bcs.encodeToByteArray<Int?>(42)))
    assertEquals("hi", bcs.decodeFromByteArray<String?>(bcs.encodeToByteArray<String?>("hi")))
  }

  @Test
  fun optionEncodesIdenticallyToNullable() {
    assertContentEquals(
      bcs.encodeToByteArray<Int?>(null),
      bcs.encodeToByteArray<Option<Int>>(Option.None),
    )
    assertContentEquals(
      bcs.encodeToByteArray<Int?>(42),
      bcs.encodeToByteArray<Option<Int>>(Option.Some(42)),
    )
  }

  @Test
  fun optionRoundTrips() {
    assertEquals(
      Option.None,
      bcs.decodeFromByteArray<Option<Int>>(bcs.encodeToByteArray<Option<Int>>(Option.None)),
    )
    assertEquals(
      Option.Some(42),
      bcs.decodeFromByteArray<Option<Int>>(bcs.encodeToByteArray<Option<Int>>(Option.Some(42))),
    )
    assertEquals(
      Option.Some("hello"),
      bcs.decodeFromByteArray<Option<String>>(
        bcs.encodeToByteArray<Option<String>>(Option.Some("hello"))
      ),
    )
  }

  @Test
  fun optionNestedInAClassRoundTrips() {
    val present = WithOption(7, Option.Some("here"))
    assertEquals(present, bcs.decodeFromByteArray<WithOption>(bcs.encodeToByteArray(present)))

    val absent = WithOption(7, Option.None)
    assertEquals(absent, bcs.decodeFromByteArray<WithOption>(bcs.encodeToByteArray(absent)))
  }

  @Test
  fun nullableNestedInAClassRoundTrips() {
    val present = WithNullable(7, "here")
    assertEquals(present, bcs.decodeFromByteArray<WithNullable>(bcs.encodeToByteArray(present)))

    val absent = WithNullable(7, null)
    assertEquals(absent, bcs.decodeFromByteArray<WithNullable>(bcs.encodeToByteArray(absent)))
    assertContentEquals(byteArrayOf(7, 0, 0, 0, 0), bcs.encodeToByteArray(absent))
  }

  @Test
  fun optionAndNullableAgreeInsideAClass() {
    assertContentEquals(
      bcs.encodeToByteArray(WithNullable(7, "here")),
      bcs.encodeToByteArray(WithOption(7, Option.Some("here"))),
    )
    assertContentEquals(
      bcs.encodeToByteArray(WithNullable(7, null)),
      bcs.encodeToByteArray(WithOption(7, Option.None)),
    )
  }

  @Test
  fun tagOtherThanZeroOrOneIsRejected() {
    val e =
      assertFailsWith<NotSupported> { bcs.decodeFromByteArray<Int?>(byteArrayOf(2, 0, 0, 0, 0)) }
    assertEquals("Invalid Option tag: 2", e.message)
  }
}
