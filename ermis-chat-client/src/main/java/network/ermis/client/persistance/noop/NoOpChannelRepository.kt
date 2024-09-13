package network.ermis.client.persistance.noop

import network.ermis.client.persistance.ChannelRepository
import network.ermis.core.models.Channel
import network.ermis.core.models.Member
import network.ermis.core.models.Message
import java.util.Date

/**
 * Repository to read and write [Channel] data.
 */
@Suppress("TooManyFunctions")
internal object NoOpChannelRepository : ChannelRepository {
    override suspend fun insertChannel(channel: Channel) { /* No-Op */ }
    override suspend fun insertChannels(channels: Collection<Channel>) { /* No-Op */ }
    override suspend fun deleteChannel(cid: String) { /* No-Op */ }
    override suspend fun deleteChannelMessage(message: Message) { /* No-Op */ }
    override suspend fun selectAllCids(): List<String> = emptyList()
    override suspend fun selectChannels(channelCIDs: List<String>): List<Channel> = emptyList()
    override suspend fun selectChannel(cid: String): Channel? = null
    override suspend fun selectChannelCidsBySyncNeeded(limit: Int): List<String> = emptyList()
    override suspend fun selectChannelsSyncNeeded(limit: Int): List<Channel> = emptyList()
    override suspend fun setChannelDeletedAt(cid: String, deletedAt: Date) { /* No-Op */ }
    override suspend fun setHiddenForChannel(cid: String, hidden: Boolean, hideMessagesBefore: Date) { /* No-Op */ }
    override suspend fun setHiddenForChannel(cid: String, hidden: Boolean) { /* No-Op */ }
    override suspend fun selectMembersForChannel(cid: String): List<Member> = emptyList()
    override suspend fun updateMembersForChannel(cid: String, members: List<Member>) { /* No-Op */ }
    override suspend fun updateLastMessageForChannel(cid: String, lastMessage: Message) { /* No-Op */ }

    override suspend fun evictChannel(cid: String) { /* No-Op */ }

    override suspend fun clear() { /* No-Op */ }
}
