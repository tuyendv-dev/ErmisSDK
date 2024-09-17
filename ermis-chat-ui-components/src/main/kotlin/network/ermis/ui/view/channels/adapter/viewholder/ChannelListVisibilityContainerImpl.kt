package network.ermis.ui.view.channels.adapter.viewholder

import network.ermis.core.models.ChannelCapabilities
import network.ermis.ui.view.ChannelListView.ChannelOptionVisibilityPredicate
import network.ermis.ui.utils.ListenerDelegate

internal class ChannelListVisibilityContainerImpl(
    isMoreOptionsVisible: ChannelOptionVisibilityPredicate = moreOptionsDefault,
    isDeleteOptionVisible: ChannelOptionVisibilityPredicate = deleteOptionDefault,
) : ChannelListVisibilityContainer {

    override var isMoreOptionsVisible: ChannelOptionVisibilityPredicate by ListenerDelegate(isMoreOptionsVisible) { realPredicate ->
        ChannelOptionVisibilityPredicate { channel ->
            realPredicate().invoke(channel)
        }
    }

    override var isDeleteOptionVisible: ChannelOptionVisibilityPredicate by ListenerDelegate(isDeleteOptionVisible) { realPredicate ->
        ChannelOptionVisibilityPredicate { channel ->
            realPredicate().invoke(channel)
        }
    }

    private companion object {
        val moreOptionsDefault: ChannelOptionVisibilityPredicate = ChannelOptionVisibilityPredicate {
            // "more options" is visible by default
            true
        }

        val deleteOptionDefault: ChannelOptionVisibilityPredicate = ChannelOptionVisibilityPredicate {
            // "delete option" is visible if the channel's ownCapabilities contains the delete capability
            it.ownCapabilities.contains(ChannelCapabilities.DELETE_CHANNEL)
        }
    }
}
