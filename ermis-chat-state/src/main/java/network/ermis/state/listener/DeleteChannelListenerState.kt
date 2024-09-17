package network.ermis.state.listener

import io.getstream.result.Error
import io.getstream.result.Result
import network.ermis.client.ErmisClient
import network.ermis.client.plugin.listeners.DeleteReactionListener
import network.ermis.client.setup.ClientState
import network.ermis.core.models.Channel
import network.ermis.core.models.User
import network.ermis.state.plugin.logic.LogicRegistry

/**
 * [DeleteReactionListener] implementation for [io.getstream.chat.android.offline.plugin.internal.OfflinePlugin].
 * Handles adding reaction to the state of the SDK.
 *
 * @param logic [LogicRegistry]
 * @param clientState [ClientState]
 */
internal class DeleteChannelListenerState(
    private val logic: LogicRegistry,
    private val clientState: ClientState,
) : network.ermis.client.plugin.listeners.DeleteChannelListener {

    /**
     * A method called before making an API call to delete a channel.
     *
     * @param currentUser The currently logged in user.
     * @param channelType The channel type. ie messaging.
     * @param channelId The channel id. ie 123.
     */
    override suspend fun onDeleteChannelRequest(
        currentUser: User?,
        channelType: String,
        channelId: String,
    ) {
    }

    /**
     * A method called after receiving the response from the delete channel call.
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
        // Nothing to be done. The Reaction is deleted optimistically.
    }

    /**
     * Runs precondition check for [ErmisClient.deleteChannel].
     * The request will be run if the method returns [Result.Success] and won't be made if it returns [Result.Failure].
     *
     * @param currentUser The currently logged in user.
     * @param channelType The channel type. ie messaging.
     * @param channelId The channel id. ie 123.
     *
     * @return [Result.Success] if the precondition is fulfilled, [Result.Failure] otherwise.
     */
    override suspend fun onDeleteChannelPrecondition(
        currentUser: User?,
        channelType: String,
        channelId: String,
    ): Result<Unit> {
        return if (currentUser != null) {
            Result.Success(Unit)
        } else {
            Result.Failure(Error.GenericError(message = "Current user is null!"))
        }
    }
}
