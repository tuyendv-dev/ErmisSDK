package network.ermis.core.models

import androidx.compose.runtime.Immutable
import network.ermis.core.models.User
import java.util.Date

@Immutable
public data class Flag(
    val user: User,
    val targetUser: User?,
    val targetMessageId: String,
    val reviewedBy: String,
    val createdByAutomod: Boolean,
    val createdAt: Date?,
    val updatedAt: Date,
    val reviewedAt: Date?,
    val approvedAt: Date?,
    val rejectedAt: Date?,
)
