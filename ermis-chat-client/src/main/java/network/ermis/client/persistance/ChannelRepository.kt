package network.ermis.client.persistance

import network.ermis.core.models.Channel
import network.ermis.core.models.Member
import network.ermis.core.models.Message
import java.util.Date

/**
 * Repository to read and write [Channel] data.
 */
@Suppress("TooManyFunctions")
public interface ChannelRepository {

    /**
     * Inserts a [Channel]
     *
     * @param channel [Channel] to insert.
     */
    public suspend fun insertChannel(channel: Channel)

    /**
     * Inserts many [Channel]s.
     *
     * @param channels collection of [Channel]
     */
    public suspend fun insertChannels(channels: Collection<Channel>)

    /**
     * Deletes a [Channel] by the cid.
     *
     * @param cid String
     */
    public suspend fun deleteChannel(cid: String)

    /**
     * Deletes a [Message] from a [Channel.messages].
     *
     * @param message [Message] to delete.
     */
    public suspend fun deleteChannelMessage(message: Message)

    /**
     * Selects all channels' cids.
     *
     * @return A list of channels' cids stored in the repository.
     */
    public suspend fun selectAllCids(): List<String>

    /**
     * Select channels by full channel IDs [Channel.cid]
     *
     * @param channelCIDs A list of [Channel.cid] as query specification.
     *
     * @return A list of channels found in repository.
     */
    public suspend fun selectChannels(channelCIDs: List<String>): List<Channel>

    /**
     * Select channel by full channel ID [Channel.cid]
     *
     * @param cid A [Channel.cid] as query specification.
     *
     * @return A channel found in repository.
     */
    public suspend fun selectChannel(cid: String): Channel?

    /**
     * Read which channel cids need sync.
     */
    public suspend fun selectChannelCidsBySyncNeeded(limit: Int = NO_LIMIT): List<String>

    /**
     * Read which channels need sync.
     */
    public suspend fun selectChannelsSyncNeeded(limit: Int = NO_LIMIT): List<Channel>

    /**
     * Sets the Channel.deleteAt for a channel.
     *
     * @param cid String.
     * @param deletedAt Date.
     */
    public suspend fun setChannelDeletedAt(cid: String, deletedAt: Date)

    /**
     * Sets the Channel.hidden for a channel.
     *
     * @param cid String.
     * @param hidden Date.
     * @param hideMessagesBefore Date.
     */
    public suspend fun setHiddenForChannel(cid: String, hidden: Boolean, hideMessagesBefore: Date)

    /**
     * Sets the Channel.hidden for a channel.
     *
     * @param cid String.
     * @param hidden Date.
     */
    public suspend fun setHiddenForChannel(cid: String, hidden: Boolean)

    /**
     * Reads the member list of a channel.
     *
     * @param cid String.
     */
    public suspend fun selectMembersForChannel(cid: String): List<Member>

    /**
     * Updates the members of a [Channel]
     *
     * @param cid String.
     * @param members list of [Member]
     */
    public suspend fun updateMembersForChannel(cid: String, members: List<Member>)

    /**
     * Updates the last message for a [Channel]
     *
     * @param cid String.
     * @param lastMessage [Message].
     */
    public suspend fun updateLastMessageForChannel(cid: String, lastMessage: Message)

    /**
     * Evict a [Channel] from the repository.
     *
     * @param cid String
     */
    public suspend fun evictChannel(cid: String)

    /**
     * Clear Channels of this repository.
     */
    public suspend fun clear()

    private companion object {
        private const val NO_LIMIT: Int = -1
    }
}
