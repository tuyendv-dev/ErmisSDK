package network.ermis.core.models

import androidx.compose.runtime.Immutable
import java.util.Date

@Immutable
public data class BannedUser(
    val user: User,
    val bannedBy: User?,
    val channel: Channel?,
    val createdAt: Date?,
    val expires: Date?,
    val shadow: Boolean,
    val reason: String?,
)
