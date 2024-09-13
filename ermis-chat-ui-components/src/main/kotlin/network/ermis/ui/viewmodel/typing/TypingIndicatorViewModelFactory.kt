
package network.ermis.ui.viewmodel.typing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import network.ermis.client.ErmisClient

/**
 * A ViewModel factory for TypingIndicatorViewModel.
 *
 * @param cid The channel id in the format messaging:123.
 * @param chatClient The [ErmisClient] instance.
 * @param messageId The id of the message we wish to focus to. Used to limit the number of channel queries as well.
 *
 * @see TypingIndicatorViewModel
 */
public class TypingIndicatorViewModelFactory(
    private val cid: String,
    private val chatClient: ErmisClient = ErmisClient.instance(),
    private val messageId: String? = null,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass == TypingIndicatorViewModel::class.java) {
            "TypingIndicatorViewModelFactory can only create instances of TypingIndicatorViewModel"
        }

        @Suppress("UNCHECKED_CAST")
        return TypingIndicatorViewModel(cid, chatClient, messageId) as T
    }

    @Suppress("NEWER_VERSION_IN_SINCE_KOTLIN")
    public class Builder
    @SinceKotlin("99999.9")
    constructor() {
        private var cid: String? = null
        private var chatClient: ErmisClient? = null
        private var messageId: String? = null

        /**
         * Sets the channel id in the format messaging:123.
         */
        public fun cid(cid: String): Builder = apply {
            this.cid = cid
        }

        /**
         * Sets the [ErmisClient] instance.
         */
        public fun chatClient(chatClient: ErmisClient): Builder = apply {
            this.chatClient = chatClient
        }

        /**
         * Sets the messageId of the [Message] we wish to scroll to.
         */
        public fun messageId(messageId: String?): Builder = apply {
            this.messageId = messageId
        }

        /**
         * Builds [TypingIndicatorViewModelFactory] instance.
         */
        public fun build(): ViewModelProvider.Factory {
            return TypingIndicatorViewModelFactory(
                cid = cid ?: error("Channel cid should not be null"),
                chatClient = chatClient ?: ErmisClient.instance(),
                messageId = messageId,
            )
        }
    }
}
