package network.ermis.client.plugin.listeners

import network.ermis.client.ErmisClient
import network.ermis.core.models.Channel
import network.ermis.core.models.User
import io.getstream.result.Result

public interface DeleteChannelListener {

    /**
     * A method called before making an API call to delete a channel.
     *
     * @param currentUser The currently logged in user.
     * @param channelType The channel type. ie messaging.
     * @param channelId The channel id. ie 123.
     */
    public suspend fun onDeleteChannelRequest(
        currentUser: User?,
        channelType: String,
        channelId: String,
    )

    /**
     * A method called after receiving the response from the delete channel call.
     *
     * @param channelType The channel type. ie messaging.
     * @param channelId The channel id. ie 123.
     * @param result The API call result.
     */
    public suspend fun onDeleteChannelResult(
        channelType: String,
        channelId: String,
        result: Result<Channel>,
    )

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
    public suspend fun onDeleteChannelPrecondition(
        currentUser: User?,
        channelType: String,
        channelId: String,
    ): Result<Unit>
}
