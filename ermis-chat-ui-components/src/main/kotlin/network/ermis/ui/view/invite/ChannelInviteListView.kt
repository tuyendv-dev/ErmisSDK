package network.ermis.ui.view.invite

import android.content.Context
import android.util.AttributeSet
import android.widget.ViewFlipper
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import network.ermis.core.models.Channel
import network.ermis.ui.components.R
import network.ermis.ui.components.databinding.UiChannelInviteListViewBinding
import network.ermis.ui.view.invite.ChannelInviteListView.ErrorEventHandler
import network.ermis.ui.utils.extensions.createStreamThemeWrapper
import network.ermis.ui.utils.extensions.showToast
import network.ermis.ui.utils.extensions.streamThemeInflater
import network.ermis.ui.viewmodel.invited.InvitedListViewModel
import network.ermis.ui.widgets.EndlessScrollListener

public class ChannelInviteListView : ViewFlipper {

    private companion object {
        private const val LOAD_MORE_THRESHOLD = 10
    }

    private object Flipper {
        const val RESULTS = 0
        const val EMPTY = 1
        const val LOADING = 2
    }

    private val binding = UiChannelInviteListViewBinding.inflate(streamThemeInflater, this)

    public constructor(context: Context) : super(context.createStreamThemeWrapper()) {
        init(null)
    }

    public constructor(context: Context, attrs: AttributeSet?) : super(context.createStreamThemeWrapper(), attrs) {
        init(attrs)
    }

    private val adapter = InviteListAdapter()

    private var loadMoreListener: LoadMoreListener? = null

    private var errorEventHandler = ErrorEventHandler { errorEvent ->
        when (errorEvent) {
            is InvitedListViewModel.ErrorEvent.AcceptInviteError -> errorEvent.streamError.message
            is InvitedListViewModel.ErrorEvent.RejectInvitedError -> errorEvent.streamError.message
        }.let(::showToast)
    }

    private val scrollListener = EndlessScrollListener(LOAD_MORE_THRESHOLD) {
        loadMoreListener?.onLoadMoreRequested()
    }

    // private lateinit var style: MentionListViewStyle

    private fun init(attrs: AttributeSet?) {
        // style = MentionListViewStyle(context, attrs).also { style ->
        //     setBackgroundColor(style.backgroundColor)
        //     binding.emptyImage.setImageDrawable(style.emptyStateDrawable)
        //     adapter.previewStyle = style.messagePreviewStyle
        // }

        binding.inviteListRecyclerView.apply {
            setHasFixedSize(true)
            adapter = this@ChannelInviteListView.adapter
            addItemDecoration(
                DividerItemDecoration(
                    context,
                    LinearLayoutManager.VERTICAL,
                ).apply {
                    setDrawable(AppCompatResources.getDrawable(context, R.drawable.divider)!!)
                },
            )
            addOnScrollListener(scrollListener)
        }
    }

    public fun showChannels(channels: List<Channel>) {
        val isEmpty = channels.isEmpty()
        displayedChild = if (isEmpty) Flipper.EMPTY else Flipper.RESULTS
        adapter.submitList(channels)
    }

    public fun showLoading() {
        displayedChild = Flipper.LOADING
        scrollListener.disablePagination()
    }

    public fun showError(errorEvent: InvitedListViewModel.ErrorEvent) {
        errorEventHandler.onErrorEvent(errorEvent)
    }

    public fun setErrorEventHandler(handler: ErrorEventHandler) {
        this.errorEventHandler = handler
    }

    public fun setChannelInviteListener(inviteListener: ChannelInviteListener?) {
        adapter.setChannelInviteListener(inviteListener)
    }

    public fun setLoadMoreListener(loadMoreListener: LoadMoreListener?) {
        this.loadMoreListener = loadMoreListener
    }

    public interface ChannelInviteListener {
        public fun onAcceptInvite(channel: Channel)
        public fun onDeclineInvite(channel: Channel)
        public fun onClickInvite(channel: Channel)
    }

    public fun interface LoadMoreListener {
        public fun onLoadMoreRequested()
    }

    public fun interface ErrorEventHandler {
        public fun onErrorEvent(errorEvent: InvitedListViewModel.ErrorEvent)
    }
}
