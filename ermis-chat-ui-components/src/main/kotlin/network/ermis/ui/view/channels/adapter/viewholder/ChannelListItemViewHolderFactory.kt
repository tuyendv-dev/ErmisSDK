
package network.ermis.ui.view.channels.adapter.viewholder

import android.content.Context
import android.view.ViewGroup
import network.ermis.ui.view.channels.ChannelListViewStyle
import network.ermis.ui.view.channels.adapter.ChannelListItem
import network.ermis.ui.view.channels.adapter.ChannelListItemViewType
import network.ermis.ui.view.channels.adapter.viewholder.internal.ChannelListLoadingMoreViewHolder
import network.ermis.ui.view.channels.adapter.viewholder.internal.ChannelViewHolder

public open class ChannelListItemViewHolderFactory {

    protected lateinit var listenerContainer: ChannelListListenerContainer
        private set

    protected lateinit var visibilityContainer: ChannelListVisibilityContainer
        private set

    protected lateinit var iconProviderContainer: ChannelListIconProviderContainer
        private set

    protected lateinit var style: ChannelListViewStyle
        private set

    internal fun setListenerContainer(listenerContainer: ChannelListListenerContainer) {
        this.listenerContainer = listenerContainer
    }

    internal fun setVisibilityContainer(visibilityContainer: ChannelListVisibilityContainer) {
        this.visibilityContainer = visibilityContainer
    }

    internal fun setIconProviderContainer(iconProviderContainer: ChannelListIconProviderContainer) {
        this.iconProviderContainer = iconProviderContainer
    }

    internal fun setStyle(style: ChannelListViewStyle) {
        this.style = style
    }

    /**
     * Returns a view type value based on the type and contents of the given [item].
     * The view type returned here will be used as a parameter in [createViewHolder].
     *
     * For built-in view types, see [ChannelListItemType] and its constants.
     */
    public open fun getItemViewType(item: ChannelListItem): Int {
        return when (item) {
            is ChannelListItem.LoadingMoreItem -> ChannelListItemViewType.LOADING_MORE
            is ChannelListItem.ChannelItem -> ChannelListItemViewType.DEFAULT
        }
    }

    /**
     * Creates a new ViewHolder to be used in the Message List.
     * The [viewType] parameter is determined by [getItemViewType].
     */
    public open fun createViewHolder(
        parentView: ViewGroup,
        viewType: Int,
    ): BaseChannelListItemViewHolder {
        return when (viewType) {
            ChannelListItemViewType.DEFAULT -> createChannelViewHolder(parentView)
            ChannelListItemViewType.LOADING_MORE -> createLoadingMoreViewHolder(parentView)
            else -> throw IllegalArgumentException("Unhandled ChannelList view type: $viewType")
        }
    }

    protected open fun createChannelViewHolder(parentView: ViewGroup): BaseChannelListItemViewHolder {
        ensureInitialized(parentView.context)

        return ChannelViewHolder(
            parentView,
            listenerContainer.channelClickListener,
            listenerContainer.channelLongClickListener,
            listenerContainer.deleteClickListener,
            listenerContainer.moreOptionsClickListener,
            listenerContainer.userClickListener,
            listenerContainer.swipeListener,
            style,
            visibilityContainer.isMoreOptionsVisible,
            visibilityContainer.isDeleteOptionVisible,
            iconProviderContainer.getMoreOptionsIcon,
            iconProviderContainer.getDeleteOptionIcon,
        )
    }

    protected open fun createLoadingMoreViewHolder(parentView: ViewGroup): BaseChannelListItemViewHolder {
        return ChannelListLoadingMoreViewHolder(parentView, style)
    }

    /**
     * Initializes the fields with the default values for testing.
     */
    private fun ensureInitialized(context: Context) {
        if (!::listenerContainer.isInitialized) {
            listenerContainer = ChannelListListenerContainerImpl()
        }
        if (!::visibilityContainer.isInitialized) {
            visibilityContainer = ChannelListVisibilityContainerImpl()
        }
        if (!::iconProviderContainer.isInitialized) {
            iconProviderContainer = ChannelListIconProviderContainerImpl()
        }
        if (!::style.isInitialized) {
            style = ChannelListViewStyle(context, null)
        }
    }
}
