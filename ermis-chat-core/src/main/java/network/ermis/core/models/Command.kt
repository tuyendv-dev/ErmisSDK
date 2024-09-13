package network.ermis.core.models

import androidx.compose.runtime.Immutable

@Immutable
public data class Command(
    val name: String,
    val description: String,
    val args: String,
    val set: String,
)
