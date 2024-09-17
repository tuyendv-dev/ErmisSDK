package network.ermis.core.models

import androidx.compose.runtime.Immutable
import java.util.Date

@Immutable
public data class Mute(
    val user: User,
    val target: User,
    val createdAt: Date,
    val updatedAt: Date,
    val expires: Date?,
)
