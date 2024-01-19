package xyz.mcxross.bcs.model

import kotlinx.serialization.Serializable
import xyz.mcxross.bcs.serializer.OptionSerializer

@Serializable(with = OptionSerializer::class)
sealed class Option<out T> {
  @Serializable class Some<out T>(val value: T) : Option<T>()

  @Serializable data object None : Option<Nothing>()
}
