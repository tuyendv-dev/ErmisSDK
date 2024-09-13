
package network.ermis.ui.view.channels

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.EdgeEffect
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import network.ermis.ui.view.ChannelListView
import network.ermis.core.models.Channel
import io.getstream.chat.android.ui.common.extensions.internal.cast
import network.ermis.ui.view.channels.adapter.ChannelListItem
import network.ermis.ui.view.channels.adapter.ChannelListItemAdapter
import network.ermis.ui.view.channels.adapter.viewholder.ChannelListIconProviderContainerImpl
import network.ermis.ui.view.channels.adapter.viewholder.ChannelListItemViewHolderFactory
import network.ermis.ui.view.channels.adapter.viewholder.ChannelListListenerContainerImpl
import network.ermis.ui.view.channels.adapter.viewholder.ChannelListVisibilityContainerImpl
import network.ermis.ui.view.channels.adapter.viewholder.internal.ChannelItemSwipeListener
import network.ermis.ui.utils.extensions.getDrawableCompat
import network.ermis.ui.widgets.internal.ScrollPauseLinearLayoutManager
import network.ermis.ui.widgets.internal.SimpleVerticalListDivider
import network.ermis.ui.widgets.internal.SnapToTopDataObserver

internal class SimpleChannelListView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
) : RecyclerView(context, attrs, defStyle) {

    private val layoutManager: ScrollPauseLinearLayoutManager
    private val scrollListener: EndReachedScrollListener = EndReachedScrollListener()
    private val dividerDecoration: SimpleVerticalListDivider = SimpleVerticalListDivider(context)

    private var endReachedListener: ChannelListView.EndReachedListener? = null

    private lateinit var viewHolderFactory: ChannelListItemViewHolderFactory

    private lateinit var adapter: ChannelListItemAdapter

    internal val listenerContainer = ChannelListListenerContainerImpl()

    internal val visibilityContainer = ChannelListVisibilityContainerImpl()

    internal val iconProviderContainer = ChannelListIconProviderContainerImpl()

    private lateinit var style: ChannelListViewStyle

    init {
        setHasFixedSize(true)
        layoutManager = ScrollPauseLinearLayoutManager(context)
        setLayoutManager(layoutManager)
        setSwipeListener(ChannelItemSwipeListener(this, layoutManager))

        addItemDecoration(dividerDecoration)
    }

    internal fun setChannelListViewStyle(style: ChannelListViewStyle) {
        this.style = style

        dividerDecoration.drawable = style.itemSeparator
        style.edgeEffectColor?.let(::setEdgeEffectColor)
    }

    private fun setEdgeEffectColor(@ColorInt edgeEffectColor: Int) {
        edgeEffectFactory = object : EdgeEffectFactory() {
            override fun createEdgeEffect(view: RecyclerView, direction: Int): EdgeEffect {
                return super.createEdgeEffect(view, direction).apply {
                    color = edgeEffectColor
                }
            }
        }
    }

    private fun requireAdapter(): ChannelListItemAdapter {
        if (::adapter.isInitialized.not()) {
            initAdapter()
        }
        return adapter
    }

    private fun initAdapter() {
        // Create default ViewHolderFactory if needed
        if (::viewHolderFactory.isInitialized.not()) {
            viewHolderFactory = ChannelListItemViewHolderFactory()
        }

        viewHolderFactory.setListenerContainer(this.listenerContainer)
        viewHolderFactory.setVisibilityContainer(this.visibilityContainer)
        viewHolderFactory.setIconProviderContainer(this.iconProviderContainer)
        viewHolderFactory.setStyle(style)

        adapter = ChannelListItemAdapter(viewHolderFactory)

        this.setAdapter(adapter)

        adapter.registerAdapterDataObserver(SnapToTopDataObserver(this))
    }

    internal fun currentChannelItemList(): List<ChannelListItem>? =
        if (::adapter.isInitialized) adapter.currentList else null

    fun setViewHolderFactory(viewHolderFactory: ChannelListItemViewHolderFactory) {
        check(::adapter.isInitialized.not()) { "Adapter was already initialized, please set ChannelListItemViewHolderFactory first" }

        this.viewHolderFactory = viewHolderFactory
    }

    fun setChannelClickListener(listener: ChannelListView.ChannelClickListener?) {
        listenerContainer.channelClickListener = listener ?: ChannelListView.ChannelClickListener.DEFAULT
    }

    fun setChannelLongClickListener(listener: ChannelListView.ChannelLongClickListener?) {
        listenerContainer.channelLongClickListener = listener ?: ChannelListView.ChannelLongClickListener.DEFAULT
    }

    fun setUserClickListener(listener: ChannelListView.UserClickListener?) {
        listenerContainer.userClickListener = listener ?: ChannelListView.UserClickListener.DEFAULT
    }

    fun setChannelDeleteClickListener(listener: ChannelListView.ChannelClickListener?) {
        listenerContainer.deleteClickListener = listener ?: ChannelListView.ChannelClickListener.DEFAULT
    }

    fun setMoreOptionsClickListener(listener: ChannelListView.ChannelClickListener?) {
        listenerContainer.moreOptionsClickListener = listener ?: ChannelListView.ChannelClickListener.DEFAULT
    }

    fun setIsMoreOptionsVisible(isMoreOptionsVisible: ChannelListView.ChannelOptionVisibilityPredicate) {
        visibilityContainer.isMoreOptionsVisible = isMoreOptionsVisible
    }

    fun setIsDeleteOptionVisible(isDeleteOptionVisible: ChannelListView.ChannelOptionVisibilityPredicate) {
        visibilityContainer.isDeleteOptionVisible = isDeleteOptionVisible
    }

    fun setMoreOptionsIconProvider(getMoreOptionsIcon: ChannelListView.ChannelOptionIconProvider) {
        iconProviderContainer.getMoreOptionsIcon = getMoreOptionsIcon
    }

    fun setDeleteOptionIconProvider(getDeleteOptionIcon: ChannelListView.ChannelOptionIconProvider) {
        iconProviderContainer.getDeleteOptionIcon = getDeleteOptionIcon
    }

    fun setSwipeListener(listener: ChannelListView.SwipeListener?) {
        listenerContainer.swipeListener = listener ?: ChannelListView.SwipeListener.DEFAULT
    }

    fun setItemSeparator(@DrawableRes drawableResource: Int) {
        dividerDecoration.drawable = context.getDrawableCompat(drawableResource)!!
    }

    fun setItemSeparatorHeight(height: Int) {
        dividerDecoration.drawableHeight = height
    }

    fun setShouldDrawItemSeparatorOnLastItem(shouldDrawOnLastItem: Boolean) {
        dividerDecoration.drawOnLastItem = shouldDrawOnLastItem
    }

    fun setOnEndReachedListener(listener: ChannelListView.EndReachedListener?) {
        endReachedListener = listener
        observeListEndRegion()
    }

    private fun observeListEndRegion() {
        addOnScrollListener(scrollListener)
    }

    fun setPaginationEnabled(enabled: Boolean) {
        scrollListener.setPaginationEnabled(enabled)
    }

    fun setChannels(channels: List<ChannelListItem>, commitCallback: () -> Unit) {
        requireAdapter().submitList(channels) {
            commitCallback()
        }
    }

    fun hasChannels(): Boolean {
        return requireAdapter().itemCount > 0
    }

    /**
     * @return if the adapter is initialized.
     */
    fun isAdapterInitialized(): Boolean {
        return ::adapter.isInitialized
    }

    internal fun getChannel(cid: String): Channel = adapter.getChannel(cid)

    override fun onVisibilityChanged(view: View, visibility: Int) {
        super.onVisibilityChanged(view, visibility)
        if (visibility == View.VISIBLE && ::adapter.isInitialized) {
            adapter.notifyDataSetChanged()
        }
    }

    private inner class EndReachedScrollListener : OnScrollListener() {
        private var enabled = false
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (SCROLL_STATE_IDLE == newState) {
                val linearLayoutManager = getLayoutManager()?.cast<LinearLayoutManager>()
                val lastVisiblePosition = linearLayoutManager?.findLastVisibleItemPosition()
                val reachedTheEnd = requireAdapter().itemCount - 1 == lastVisiblePosition
                if (reachedTheEnd && enabled) {
                    endReachedListener?.onEndReached()
                }
            }
        }

        fun setPaginationEnabled(enabled: Boolean) {
            this.enabled = enabled
        }
    }
}
