<h1 align="center">KMP BSC</h1>

Kotlin Multiplatform implementation of Binary Canonical Serialization (BCS) as an encoding format for the [kotlinx.serialization](https://kotlinlang.org/docs/serialization.html#libraries) library

[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)

### Table of contents
- [Quick start](#quick-start)

## Quick Start

### Installation

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
    Data(1_000_000, 3.14159265359, "BCS", false)
)
```

As a result, you get a ` BcsDataOutputBuffer` containing the state of this object in the BCS format

To deserialize an object from BCS, use the `decodeFromBinary()` function:

```kotlin
val obj = Bcs.decodeFromBinary<Data>(listOf())
```