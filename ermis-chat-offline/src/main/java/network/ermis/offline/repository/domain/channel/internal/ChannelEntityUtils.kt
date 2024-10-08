package network.ermis.offline.repository.domain.channel.internal

/**
 * Composes a short version of [ChannelEntity.toString] with the last message information only.
 */
internal fun ChannelEntity.lastMessageInfo(): String {
    return "ChannelEntity(lastMessageId: $lastMessageId, lastMessageAt: $lastMessageAt, cid: $cid)"
}
