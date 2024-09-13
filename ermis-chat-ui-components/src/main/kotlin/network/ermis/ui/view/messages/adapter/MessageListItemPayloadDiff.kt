
package network.ermis.ui.view.messages.adapter

public data class MessageListItemPayloadDiff(
    val text: Boolean,
    val replyText: Boolean,
    val reactions: Boolean,
    val attachments: Boolean,
    val replies: Boolean,
    val syncStatus: Boolean,
    val deleted: Boolean,
    val positions: Boolean,
    val pinned: Boolean,
    val user: Boolean,
    val mentions: Boolean,
    val footer: Boolean,
) {
    public operator fun plus(other: MessageListItemPayloadDiff): MessageListItemPayloadDiff {
        return MessageListItemPayloadDiff(
            text = text || other.text,
            replyText = replyText || other.replyText,
            reactions = reactions || other.reactions,
            attachments = attachments || other.attachments,
            replies = replies || other.replies,
            syncStatus = syncStatus || other.syncStatus,
            deleted = deleted || other.deleted,
            positions = positions || other.positions,
            pinned = pinned || other.pinned,
            user = user || other.user,
            mentions = mentions || other.mentions,
            footer = footer || other.footer,
        )
    }
}
