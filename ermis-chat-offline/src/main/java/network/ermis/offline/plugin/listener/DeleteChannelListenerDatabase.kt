package network.ermis.offline.plugin.listener

import io.getstream.result.Result
import network.ermis.client.persistance.ChannelRepository
import network.ermis.client.persistance.UserRepository
import network.ermis.client.plugin.listeners.DeleteChannelListener
import network.ermis.client.setup.ClientState
import network.ermis.core.models.Channel
import network.ermis.core.models.User

/**
 * [DeleteChannelListener] implementation for [io.getstream.chat.android.offline.plugin.internal.OfflinePlugin].
 * Handles creating the channel offline and updates the database.
 * Does not perform optimistic UI update as it's impossible to determine whether a particular channel should be visible
 * for the current user or not.
 *
 * @param clientState [ClientState]
 * @param channelRepository [ChannelRepository] to cache intermediate data and final result of channels.
 * @param userRepository [UserRepository] Requests users from database.
 */
internal class DeleteChannelListenerDatabase(
    private val clientState: ClientState,
    private val channelRepository: ChannelRepository,
    private val userRepository: UserRepository,
) : network.ermis.client.plugin.listeners.DeleteChannelListener {

    /**
     * A method called before making an API call to create the channel.
     * Creates the channel based on provided data and updates the database.
     * Channel's id will be automatically generated based on the members list if provided id is empty.
     *
     * @param channelType The channel type. ie messaging.
     * @param channelId The channel id. ie 123.
     * @param memberIds The list of members' ids.
     * @param extraData Map of key-value pairs that let you store extra data
     * @param currentUser The currently logged in user.
     */
    override suspend fun onDeleteChannelRequest(
        currentUser: User?,
        channelType: String,
        channelId: String,
    ) {
    }

    /**
     * A method called after receiving the response from the create channel call.
     * Updates channel's sync status stored in the database based on API result.
     *
     * @param channelType The channel type. ie messaging.
     * @param channelId The channel id. ie 123.
     * @param result The API call result.
     */
    override suspend fun onDeleteChannelResult(
        channelType: String,
        channelId: String,
        result: Result<Channel>,
    ) {
    }

    /**
     * Checks if current user is set and channel's id conditions are met.
     *
     * @param currentUser The currently logged in user.
     * @param channelType The channel type. ie messaging.
     * @param channelId The channel id. ie 123.
     */
    override suspend fun onDeleteChannelPrecondition(
        currentUser: User?,
        channelType: String,
        channelId: String,
    ): Result<Unit> {
        return Result.Success(Unit)
    }
}
