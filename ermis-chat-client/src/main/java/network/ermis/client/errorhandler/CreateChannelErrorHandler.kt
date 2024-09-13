package network.ermis.client.errorhandler

import network.ermis.core.models.Channel
import io.getstream.result.Result
import io.getstream.result.call.Call
import io.getstream.result.call.ReturnOnErrorCall

public interface CreateChannelErrorHandler {

    /**
     * Returns a [Result] from this side effect when original request fails.
     *
     * @param originalCall The original call.
     * @param channelType The channel type. ie messaging.
     * @param channelId The channel id. ie 123.
     * @param memberIds The list of members' ids.
     * @param extraData Map of key-value pairs that let you store extra data
     *
     * @return result The replacement for the original result.
     */
    public fun onCreateChannelError(
        originalCall: Call<Channel>,
        channelType: String,
        channelId: String,
        memberIds: List<String>,
        extraData: Map<String, Any>,
    ): ReturnOnErrorCall<Channel>
}

internal fun Call<Channel>.onCreateChannelError(
    errorHandlers: List<CreateChannelErrorHandler>,
    channelType: String,
    channelId: String,
    memberIds: List<String>,
    extraData: Map<String, Any>,
): Call<Channel> {
    return errorHandlers.fold(this) { createChannelCall, errorHandler ->
        errorHandler.onCreateChannelError(
            originalCall = createChannelCall,
            channelId = channelId,
            channelType = channelType,
            memberIds = memberIds,
            extraData = extraData,
        )
    }
}
