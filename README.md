<h1 align="center">BCS</h1>

Kotlin Multiplatform implementation of Binary Canonical Serialization (BCS) as an encoding format for
the [kotlinx.serialization](https://kotlinlang.org/docs/serialization.html#libraries) library

[![Kotlin Version](https://img.shields.io/badge/Kotlin-1.9.22-B125EA?logo=kotlin)](https://kotlinlang.org)
[![Maven Central](https://img.shields.io/maven-central/v/xyz.mcxross.bcs/bcs.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/xyz.mcxross.bcs/bcs)
[![Snapshot](https://img.shields.io/nexus/s/xyz.mcxross.bcs/bcs?server=https%3A%2F%2Fs01.oss.sonatype.org&label=Snapshot)](https://s01.oss.sonatype.org/content/repositories/snapshots/xyz/mcxross/bcs/)
[![Build](https://github.com/mcxross/kotlinx-serialization-bcs/actions/workflows/build.yml/badge.svg?branch=master)](https://github.com/mcxross/kotlinx-serialization-bcs/actions/workflows/build.yml)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)

![badge-android](http://img.shields.io/badge/Platform-Android-brightgreen.svg?logo=android)
![badge-ios](http://img.shields.io/badge/Platform-iOS-orange.svg?logo=apple)
![badge-js](http://img.shields.io/badge/Platform-NodeJS-yellow.svg?logo=javascript)
![badge-jvm](http://img.shields.io/badge/Platform-JVM-red.svg?logo=openjdk)
![badge-linux](http://img.shields.io/badge/Platform-Linux-lightgrey.svg?logo=linux)
![badge-macos](http://img.shields.io/badge/Platform-macOS-orange.svg?logo=apple)
![badge-windows](http://img.shields.io/badge/Platform-Windows-blue.svg?logo=windows)

### Table of contents

- [Quick start](#quick-start)
  - [Installation](#installation)
    - [Multiplatform](#multiplatform)
    - [Platform specific (Android, JS, Native, JVM)](#platform-specific-android-js-native-jvm)
    - [Serialization](#serialization)
      - [Basic Types](#basic-types)
      - [User-defined Types](#user-defined-types)
- [For Rust Developers](#for-rust-developers)
  - [Parity](#parity)
- [Reference](#reference)
- [Contribution](#contribution)

## Quick Start

### Installation

#### Multiplatform
Add the `kotlinx-serialization-bcs` dependency to the common sourceSet

```kotlin
implementation("xyz.mcxross.bcs:bcs:<$bcs_version>")
```
#### Platform specific (Android, JS, Native, JVM)
Add the `kotlinx-serialization-bcs` dependency to the Project's dependency block

Generic:

```kotlin
implementation("xyz.mcxross.bcs:<bcs-[platform]>:<$bcs_version>")
```
For example for Android and JS

Android:

```kotlin
implementation("xyz.mcxross.bcs:bcs-android:<$bcs_version>")
```

JS:

```kotlin
implementation("xyz.mcxross.bcs:bcs-js:<$bcs_version>")
```


#### Serialization

##### Basic Types

For basic types, simply call the `Bcs.encodeToByteArray()` function with the desired value, and in each case, you will get a `ByteArray` containing the state of this object in the BCS format

- **Triple**

```kotlin
val bcs = Bcs.encodeToByteArray(Triple(listOf(), "çå∞≠¢õß∂ƒ∫", Location(x = 3, y = 4)))
```

- **String**

```kotlin
val bcs = Bcs.encodeToByteArray("çå∞≠¢õß∂ƒ∫")
```

- **List**

```kotlin
val bcs = Bcs.encodeToByteArray(listOf(1, 2, 3, 4, 5))
```

- **Map**

```kotlin
val bcs = Bcs.encodeToByteArray(mapOf("a" to 1, "b" to 2, "c" to 3))
```

- **Double**

```kotlin
val bcs = Bcs.encodeToByteArray(3.14159265359)
```

- **Boolean**

```kotlin
val bcs = Bcs.encodeToByteArray(false)
```

Deserialization is done by calling the `Bcs.decodeFromByteArray()` function with the **described** type

##### User-defined Types

For use-defined types, first make a class serializable by annotating it with `@Serializable`

```kotlin
@Serializable
data class Data(
    val long: Long,
    val double: Double,
    val string: String,
    val boolean: Boolean
)
```

You can now serialize an instance of this class by calling `Bcs.encodeToByteArray()`

```kotlin
val bcs = Bcs.encodeToByteArray(
    Data(1_000_000, 3.14159265359, "çå∞≠¢õß∂ƒ∫", false)
)
```

As a result, you get a `ByteArray` containing the state of this object in the BCS format

To deserialize an object from BCS, use the `decodeFromByteArray()` function:

```kotlin
val obj = Bcs.decodeFromByteArray<Data>(byteArrayOf())
```

**Note**: BCS is not a self-describing format. As such, one must know the message type and layout ahead of time in order
to deserialize.

## For Rust Developers

### Parity

For parity with the original Rust BCS implementation, the following type translations are used:

| Rust type | Kotlin Type        |
|-----------|--------------------|
| Struct    | Data class         |
| Tuple     | Pair, Triple, etc. |
| Unit      | Unit               |
| Option    | T?                 |

## Reference

BCS specs can be found [here](https://github.com/diem/bcs/#readme)

## Contribution

All contributions to KMP BCS are welcome. Before opening a PR, please submit an issue detailing the bug or feature. When
opening a PR, please ensure that your contribution builds on the KMM toolchain, has been linted
with `ktfmt <GOOGLE (INTERNAL)>`, and contains tests when applicable. For more information, please see
the [contribution guidelines](CONTRIBUTING.md).

## License

    Copyright 2022 McXross

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

[maven-central]: https://search.maven.org/artifact/xyz.mcxross/bcs