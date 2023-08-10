package xyz.mcxross.bcs.ext

fun Boolean.toByte() : Byte = if (this) 1 else 0

fun Byte.toBoolean() : Boolean = this.toInt() != 0
