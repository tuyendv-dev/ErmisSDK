package network.ermis.state.errorhandler.internal

import network.ermis.client.errorhandler.CreateChannelErrorHandler
import network.ermis.client.persistance.ChannelRepository
import network.ermis.client.setup.ClientState
import network.ermis.client.utils.channel.generateChannelIdIfNeeded
import network.ermis.core.models.Channel
import io.getstream.result.Error
import io.getstream.result.Result
import io.getstream.result.call.Call
import io.getstream.result.call.ReturnOnErrorCall
import io.getstream.result.call.onErrorReturn
import kotlinx.coroutines.CoroutineScope

/**
 * Returns a [Channel] instance if the channel was created offline and can be synced.
 *
 * @param scope [CoroutineScope]
 * @param channelRepository [ChannelRepository]
 * @param clientState [ClientState]
 */
internal class CreateChannelErrorHandlerImpl(
    private val scope: CoroutineScope,
    private val channelRepository: ChannelRepository,
    private val clientState: ClientState,
) : CreateChannelErrorHandler {

    /**
     * Replaces the original response error if the user is offline and the channel exists in the cache.
     * This means that the channel was created locally but the API request failed due to lack of connection.
     * The request will be synced once user's connection is recovered.
     *
     * @param originalCall The original call.
     * @param channelType The channel type. ie messaging.
     * @param channelId The channel id. ie 123.
     * @param memberIds The list of members' ids.
     * @param extraData Map of key-value pairs that let you store extra data
     *
     * @return result The original or offline related result.
     */
    override fun onCreateChannelError(
        originalCall: Call<Channel>,
        channelType: String,
        channelId: String,
        memberIds: List<String>,
        extraData: Map<String, Any>,
    ): ReturnOnErrorCall<Channel> {
        return originalCall.onErrorReturn(scope) { originalError ->
            if (clientState.isOnline) {
                Result.Failure(originalError)
            } else {
                val generatedCid =
                    "$channelType:${generateChannelIdIfNeeded(channelId = channelId, memberIds = memberIds)}"
                val cachedChannel = channelRepository.selectChannels(listOf(generatedCid)).firstOrNull()
                if (cachedChannel == null) {
                    Result.Failure(Error.GenericError(message = "Channel wasn't cached properly."))
                } else {
                    Result.Success(cachedChannel)
                }
            }
        }
    }
}
