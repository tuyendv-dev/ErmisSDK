
package network.ermis.ui.viewmodel.typing

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import network.ermis.client.ErmisClient
import network.ermis.client.channel.state.ChannelState
import network.ermis.core.models.User
import network.ermis.state.extensions.watchChannelAsState
import network.ermis.ui.widgets.typing.TypingIndicatorView
import io.getstream.log.taggedLogger
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

/**
 * ViewModel used by [TypingIndicatorView].
 * It is responsible for updating the state of users who are currently typing.
 *
 * @param cid The full channel id, i.e. "messaging:123".
 * @param chatClient The main entry point for all low-level chat operations.
 * @param messageId The id of a message we wish to scroll to in messages list. Used to control the number of channel
 * queries executed on screen initialization.
 */
public class TypingIndicatorViewModel(
    private val cid: String,
    private val chatClient: ErmisClient = ErmisClient.instance(),
    private val messageId: String? = null,
) : ViewModel() {

    private val logger by taggedLogger(TAG)

    /**
     * Holds information about the current channel and is actively updated.
     */
    private val channelState: StateFlow<ChannelState?> = observeChannelState()

    /**
     * A list of users who are currently typing.
     */
    public val typingUsers: LiveData<List<User>> =
        channelState.filterNotNull().flatMapLatest { it.typing }.map { typingEvent ->
            typingEvent.users
        }.asLiveData()

    private fun observeChannelState(): StateFlow<ChannelState?> {
        val messageLimit = if (messageId != null) 0 else DEFAULT_MESSAGES_LIMIT
        logger.d { "[observeChannelState] cid: $cid, messageLimit: $messageLimit" }
        return chatClient.watchChannelAsState(
            cid = cid,
            messageLimit = messageLimit,
            coroutineScope = viewModelScope,
        )
    }

    private companion object {

        private const val TAG = "Chat:TypingIndicatorVM"

        /**
         * The default limit for messages that will be requested.
         */
        private const val DEFAULT_MESSAGES_LIMIT: Int = 30
    }
}
