package xyz.mcxross.bcs.ext

import xyz.mcxross.bcs.stream.BcsDataInputBuffer

internal fun Boolean.toByte(): Byte = if (this) 1 else 0

internal fun Byte.toBoolean(): Boolean = this.toInt() != 0

internal fun ByteArray.toBcsBuffer() = BcsDataInputBuffer(this)
