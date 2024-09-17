package network.ermis.core.models

import androidx.compose.runtime.Immutable
import network.ermis.core.models.querysort.ComparableFieldProvider
import java.util.Date

/**
 * Represents a channel member.
 */
@Immutable
public data class Member(
    /**
     * The user who is a member of the channel.
     */
    override val user: User,

    /**
     * When the user became a member.
     */
    val createdAt: Date? = null,

    /**
     * When the membership data was last updated.
     */
    val updatedAt: Date? = null,

    /**
     * If the user is invited.
     */
    val isInvited: Boolean? = null,

    /**
     * The date the invite was accepted.
     */
    val inviteAcceptedAt: Date? = null,

    /**
     * The date the invite was rejected.
     */
    val inviteRejectedAt: Date? = null,

    /**
     * If channel member is shadow banned.
     */
    val shadowBanned: Boolean? = false,

    /**
     * If channel member is banned.
     */
    val banned: Boolean = false,

    /**
     * The user's channel-level role.
     */
    val channelRole: String? = null,
) : UserEntity, ComparableFieldProvider {

    override fun getComparableField(fieldName: String): Comparable<*>? {
        return when (fieldName) {
            "user_id", "userId" -> getUserId()
            "created_at", "createdAt" -> createdAt
            "updated_at", "updatedAt" -> updatedAt
            "is_invited", "isInvited" -> isInvited
            "invite_accepted_at", "inviteAcceptedAt" -> inviteAcceptedAt
            "invite_rejected_at", "inviteRejectedAt" -> inviteRejectedAt
            "shadow_banned", "shadowBanned" -> shadowBanned
            "banned" -> banned
            "channel_role", "channelRole" -> channelRole
            else -> null
        }
    }
}
