<h1 align="center">KMP BSC</h1>

Kotlin Multiplatform implementation of Binary Canonical Serialization (BCS) as an encoding format for
the [kotlinx.serialization](https://kotlinlang.org/docs/serialization.html#libraries) library

[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)

### Table of contents

- [Quick start](#quick-start)
- [Type Translation](#type-translation)
- [Reference](#reference)
- [Contribution](#contribution)

## Quick Start

### Installation

TBA

#### Serialization

First, make a class serializable by annotating it with `@Serializable`

```kotlin
@Serializable
data class Data(
    val long: Long,
    val double: Double,
    val string: String,
    val boolean: Boolean
)
```

You can now serialize an instance of this class by calling `Bcs.encodeToBinary()`

```kotlin
val bcs = Bcs.encodeToBinary(
    Data(1_000_000, 3.14159265359, "çå∞≠¢õß∂ƒ∫", false)
)
```

As a result, you get a `ByteArray` containing the state of this object in the BCS format

To deserialize an object from BCS, use the `decodeFromBinary()` function:

```kotlin
val obj = Bcs.decodeFromBinary<Data>(byteArrayOf())
```

**Note**: BCS is not a self-describing format. As such, one must know the message type and layout ahead of time in order
to deserialize.

## Type Translation

| Rust type | Kotlin Type        |
|-----------|--------------------|
| Struct    | Data class         |
| Tuple     | Pair, Triple, etc. |
| Unit      | Unit               |
| Option    | T?                 |

## Reference

BCS specs can be found [here](https://github.com/diem/bcs/#readme)

## Contribution

All contributions to KMP BSC are welcome. Before opening a PR, please submit an issue detailing the bug or feature. When
opening a PR, please ensure that your contribution builds on the KMM toolchain, has been linted
with `ktfmt <GOOGLE (INTERNAL)>`, and contains tests when applicable. For more information, please see
the [contribution guidelines](CONTRIBUTING.md).
