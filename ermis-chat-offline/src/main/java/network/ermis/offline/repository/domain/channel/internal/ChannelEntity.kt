package network.ermis.offline.repository.domain.channel.internal

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import network.ermis.core.models.SyncStatus
import network.ermis.offline.repository.domain.channel.member.MemberEntity
import network.ermis.offline.repository.domain.channel.userread.ChannelUserReadEntity
import java.util.Date

/**
 * ChannelEntity stores both the channel information as well as references to all of the channel's state.
 * Messages are stored on their own table for easier pagination and updates.
 *
 * @param type Type of the channel.
 * @param channelId Channel's unique ID.
 * @param name Channel's name.
 * @param image Channel's image.
 * @param cooldown Cooldown period after sending each message in seconds.
 * @param createdByUserId Id of the user who created the channel.
 * @param frozen If the channel is frozen or not (new messages wont be allowed).
 * @param hidden If the channel is hidden (new messages changes this field to false).
 * @param hideMessagesBefore Messages before this date are hidden from the user.
 * @param members The list of channel's members.
 * @param memberCount Number of members in the channel.
 * @param watcherIds The list of watchers` ids.
 * @param watcherCount Number of watchers in the channel.
 * @param reads The list of read states.
 * @param lastMessageAt Date of the last message sent.
 * @param lastMessageId The id of the last message.
 * @param createdAt Date/time of creation.
 * @param updatedAt Date/time of the last update.
 * @param deletedAt Date/time of deletion.
 * @param extraData A map of custom fields for the channel.
 * @param syncStatus If the channel has been synced.
 * @param team Team the channel belongs to (multi-tenant only).
 * @param ownCapabilities Channel's capabilities available for the current user. Note that the field is not provided in the events.
 * @param membership Represents relationship of the current user to this channel.
 */
@Entity(tableName = CHANNEL_ENTITY_TABLE_NAME, indices = [Index(value = ["syncStatus"])])
internal data class ChannelEntity(
    val type: String,
    val channelId: String,
    val name: String,
    val description: String,
    val image: String,
    val cooldown: Int,
    val createdByUserId: String,
    val frozen: Boolean,
    val hidden: Boolean?,
    val hideMessagesBefore: Date?,
    val members: Map<String, MemberEntity>,
    val memberCount: Int,
    val watcherIds: List<String>,
    val watcherCount: Int,
    val reads: Map<String, ChannelUserReadEntity>,
    val lastMessageAt: Date?,
    val lastMessageId: String?,
    val createdAt: Date?,
    val updatedAt: Date?,
    val deletedAt: Date?,
    val extraData: Map<String, Any>,
    val syncStatus: SyncStatus,
    val team: String,
    val ownCapabilities: Set<String>,
    val memberCapabilities: Set<String>,
    val membership: MemberEntity?,
) {
    /**
     * The channel id in the format messaging:123.
     */
    @PrimaryKey
    var cid: String = "%s:%s".format(type, channelId)
}

internal const val CHANNEL_ENTITY_TABLE_NAME = "stream_chat_channel_state"
