
package network.ermis.ui.viewmodel.channels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import network.ermis.core.models.Channel
import network.ermis.core.models.FilterObject
import network.ermis.core.models.Filters
import network.ermis.core.models.querysort.QuerySorter
import network.ermis.state.event.chat.factory.ChatEventHandlerFactory

/**
 * Creates a channels view model factory.
 *
 * @param filter How to filter the channels.
 * @param sort How to sort the channels, defaults to last_updated.
 * @param limit How many channels to return.
 * @param memberLimit The number of members per channel.
 * @param messageLimit The number of messages to fetch for each channel.
 * @param chatEventHandlerFactory The instance of [ChatEventHandlerFactory] that will be used to create [ChatEventHandler].
 *
 * @see Filters
 * @see QuerySorter
 */
public class ChannelListViewModelFactory @JvmOverloads constructor(
    private val filter: FilterObject? = null,
    private val sort: QuerySorter<Channel> = ChannelListViewModel.DEFAULT_SORT,
    private val limit: Int = ChannelListViewModel.DEFAULT_CHANNEL_LIMIT,
    private val messageLimit: Int = ChannelListViewModel.DEFAULT_MESSAGE_LIMIT,
    private val memberLimit: Int = ChannelListViewModel.DEFAULT_MEMBER_LIMIT,
    private val chatEventHandlerFactory: ChatEventHandlerFactory = ChatEventHandlerFactory(),
) : ViewModelProvider.Factory {

    /**
     * Returns an instance of [ChannelListViewModel].
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass == ChannelListViewModel::class.java) {
            "ChannelListViewModelFactory can only create instances of ChannelListViewModel"
        }

        @Suppress("UNCHECKED_CAST")
        return ChannelListViewModel(
            filter = filter,
            sort = sort,
            limit = limit,
            messageLimit = messageLimit,
            memberLimit = memberLimit,
            chatEventHandlerFactory = chatEventHandlerFactory,
        ) as T
    }

    @Suppress("NEWER_VERSION_IN_SINCE_KOTLIN")
    public class Builder
    @SinceKotlin("99999.9")
    constructor() {

        private var filter: FilterObject? = null
        private var sort: QuerySorter<Channel> = ChannelListViewModel.DEFAULT_SORT
        private var limit: Int = ChannelListViewModel.DEFAULT_CHANNEL_LIMIT
        private var messageLimit: Int = ChannelListViewModel.DEFAULT_MESSAGE_LIMIT
        private var memberLimit: Int = ChannelListViewModel.DEFAULT_MEMBER_LIMIT
        private var chatEventHandlerFactory: ChatEventHandlerFactory = ChatEventHandlerFactory()

        /**
         * Sets the way to filter the channels.
         */
        public fun filter(filter: FilterObject): Builder = apply {
            this.filter = filter
        }

        /**
         * Sets the way to sort the channels, defaults to last_updated.
         */
        public fun sort(sort: QuerySorter<Channel>): Builder = apply {
            this.sort = sort
        }

        /**
         * Sets the number of channels to return.
         */
        public fun limit(limit: Int): Builder = apply {
            this.limit = limit
        }

        /**
         * Sets the number of messages to fetch for each channel.
         */
        public fun messageLimit(messageLimit: Int): Builder = apply {
            this.messageLimit = messageLimit
        }

        /**
         * Sets the number of members per channel.
         */
        public fun memberLimit(memberLimit: Int): Builder = apply {
            this.memberLimit = memberLimit
        }

        /**
         * The instance of [ChatEventHandlerFactory] that will be used to create [ChatEventHandler].
         */
        public fun chatEventHandlerFactory(chatEventHandlerFactory: ChatEventHandlerFactory): Builder = apply {
            this.chatEventHandlerFactory = chatEventHandlerFactory
        }

        /**
         * Builds [ChannelListViewModelFactory] instance.
         */
        public fun build(): ViewModelProvider.Factory {
            return ChannelListViewModelFactory(
                filter = filter,
                sort = sort,
                limit = limit,
                messageLimit = messageLimit,
                memberLimit = memberLimit,
                chatEventHandlerFactory,
            )
        }
    }
}
