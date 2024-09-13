package network.ermis.state.plugin.logic.channel

import network.ermis.client.api.models.QueryChannelRequest
import network.ermis.state.plugin.state.channel.ChannelMutableState

internal class SearchLogic(private val mutableState: ChannelMutableState) {

    private var isInsideSearch = false

    fun handleMessageBounds(request: QueryChannelRequest, noMoreMessages: Boolean) {
        when {
            !isInsideSearch && request.isFilteringAroundIdMessages() -> {
                updateSearchState(true)
            }

            isInsideSearch && request.isFilteringNewerMessages() && noMoreMessages -> {
                updateSearchState(false)
            }

            !request.isNotificationUpdate && !request.isFilteringMessages() && request.messagesLimit() != 0 -> {
                updateSearchState(false)
            }
        }
    }

    private fun updateSearchState(isInsideSearch: Boolean) {
        this.isInsideSearch = isInsideSearch
        mutableState.setInsideSearch(isInsideSearch)
    }
}
