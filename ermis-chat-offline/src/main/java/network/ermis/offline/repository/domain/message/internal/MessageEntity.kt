package network.ermis.offline.repository.domain.message.internal

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import network.ermis.core.models.SyncStatus
import network.ermis.offline.repository.domain.message.attachment.AttachmentEntity
import network.ermis.offline.repository.domain.message.channelinfo.ChannelInfoEntity
import network.ermis.offline.repository.domain.reaction.ReactionEntity
import java.util.Date

internal data class MessageEntity(
    @Embedded val messageInnerEntity: MessageInnerEntity,
    @Relation(entity = AttachmentEntity::class, parentColumn = "id", entityColumn = "messageId")
    val attachments: List<AttachmentEntity>,
    /** the reactions from the current user */
    @Relation(entity = ReactionEntity::class, parentColumn = "id", entityColumn = "messageId")
    val ownReactions: List<ReactionEntity> = emptyList(),
    /** the last 5 reactions on this message */
    @Relation(entity = ReactionEntity::class, parentColumn = "id", entityColumn = "messageId")
    val latestReactions: List<ReactionEntity> = emptyList(),
)

@Entity(
    tableName = MESSAGE_ENTITY_TABLE_NAME,
    indices = [
        Index(value = ["cid", "createdAt"]),
        Index(value = ["syncStatus"]),
    ],
)
internal data class MessageInnerEntity(
    @PrimaryKey
    val id: String,
    val cid: String,
    val userId: String,
    /** the message text */
    val text: String = "",
    /** the message text formatted as html **/
    val html: String = "",
    /** message type can be system, regular or ephemeral */
    val type: String = "",
    /** if the message has been synced to the servers, default is synced */
    val syncStatus: SyncStatus = SyncStatus.COMPLETED,
    /** the number of replies */
    val replyCount: Int = 0,
    /** the number of deleted replies */
    val deletedReplyCount: Int = 0,
    /** when the message was created */
    val createdAt: Date? = null,
    /** when the message was created locally */
    val createdLocallyAt: Date? = null,
    /** when the message was updated */
    val updatedAt: Date? = null,
    /** when the message was updated locally */
    val updatedLocallyAt: Date? = null,
    /** when the message was deleted */
    val deletedAt: Date? = null,
    /** the users mentioned in this message */
    val remoteMentionedUserIds: List<String> = emptyList(),
    /** the users to be mentioned in this message */
    val mentionedUsersId: List<String> = emptyList(),
    /** a mapping between reaction type and the count, ie like:10, heart:4 */
    val reactionCounts: Map<String, Int> = emptyMap(),
    /** a mapping between reaction type and the reaction score, ie like:10, heart:4 */
    val reactionScores: Map<String, Int> = emptyMap(),
    /** parent id, used for threads */
    val parentId: String? = null,
    /** slash command like /giphy etc */
    val command: String? = null,
    /** if the message was sent by shadow banned user */
    val shadowed: Boolean = false,
    /** message internationalization mapping.*/
    val i18n: Map<String, String> = emptyMap(),
    /** if the message is also shown in the channel **/
    val showInChannel: Boolean = false,
    @Embedded(prefix = "channel_info")
    val channelInfo: ChannelInfoEntity? = null,
    /** if the message is silent  **/
    val silent: Boolean = false,
    /** all the custom data provided for this message */
    val extraData: Map<String, Any> = emptyMap(),
    /** the ID of the quoted message **/
    val replyToId: String?,
    /** whether message is pinned or not **/
    val pinned: Boolean,
    /** date when the message got pinned **/
    val pinnedAt: Date? = null,
    /** date when pinned message expires **/
    val pinExpires: Date? = null,
    /** the ID of the user who pinned the message **/
    val pinnedByUserId: String?,
    /** participants of thread replies */
    val threadParticipantsIds: List<String> = emptyList(),
    /**
     * If the message should skip triggering a push notification when sent. Used when sending a new message.
     * Note: This property is local only, it is not sent to the backend.
     */
    var skipPushNotification: Boolean = false,

    /**
     * If the message should skip enriching the URL. If URl is not enriched, it will not be
     * displayed as a link attachment. Used when sending or updating a message.
     * Note: This property is local only, it is not sent to the backend.
     */
    var skipEnrichUrl: Boolean = false,

    /** Contains moderation details of the message **/
    val moderationDetails: ModerationDetailsEntity? = null,
)

internal const val MESSAGE_ENTITY_TABLE_NAME = "stream_chat_message"
