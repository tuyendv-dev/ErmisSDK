
package network.ermis.ui.view.pinned

import android.content.Context
import android.text.Html
import android.util.AttributeSet
import android.widget.Toast
import android.widget.ViewFlipper
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import network.ermis.core.models.Message
import network.ermis.ui.components.R
import network.ermis.ui.components.databinding.PinnedMessageListViewBinding
import network.ermis.ui.utils.extensions.createStreamThemeWrapper
import network.ermis.ui.utils.extensions.streamThemeInflater
import network.ermis.ui.widgets.EndlessScrollListener

public class PinnedMessageListView : ViewFlipper {

    private companion object {
        const val LOAD_MORE_THRESHOLD = 10
    }

    private object Flipper {
        const val RESULTS = 0
        const val EMPTY = 1
        const val LOADING = 2
    }

    private val binding = PinnedMessageListViewBinding.inflate(streamThemeInflater, this)

    public constructor(context: Context) : super(context.createStreamThemeWrapper()) {
        init(null)
    }

    public constructor(context: Context, attrs: AttributeSet?) : super(context.createStreamThemeWrapper(), attrs) {
        init(attrs)
    }

    private var loadMoreListener: LoadMoreListener? = null

    private val adapter = PinnedMessageListAdapter()

    private val scrollListener = EndlessScrollListener(LOAD_MORE_THRESHOLD) {
        loadMoreListener?.onLoadMoreRequested()
    }

    private lateinit var style: PinnedMessageListViewStyle

    private fun init(attrs: AttributeSet?) {
        style = PinnedMessageListViewStyle(context, attrs).also { style ->
            setBackgroundColor(style.backgroundColor)
            binding.emptyImage.setImageDrawable(style.emptyStateDrawable)
            adapter.messagePreviewStyle = style.messagePreviewStyle
        }

        binding.pinnedMessageListRecyclerView.apply {
            setHasFixedSize(true)
            adapter = this@PinnedMessageListView.adapter
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

        binding.emptyDescriptionTextView.text = Html.fromHtml(
            context.getString(R.string.ermis_ui_pinned_message_list_empty_description),
        )
    }

    public fun showMessages(messages: List<Message>) {
        val isEmpty = messages.isEmpty()

        displayedChild = if (isEmpty) Flipper.EMPTY else Flipper.RESULTS

        adapter.submitList(messages)
        scrollListener.enablePagination()
    }

    public fun showLoading() {
        if (adapter.itemCount == 0) {
            displayedChild = Flipper.LOADING
        }
        scrollListener.disablePagination()
    }

    public fun showError() {
        Toast.makeText(context, R.string.ermis_ui_pinned_message_list_results_error, Toast.LENGTH_SHORT).show()
    }

    public fun setPinnedMessageSelectedListener(pinnedMessageSelectedListener: PinnedMessageSelectedListener?) {
        adapter.setPinnedMessageSelectedListener(pinnedMessageSelectedListener)
    }

    public fun setLoadMoreListener(loadMoreListener: LoadMoreListener?) {
        this.loadMoreListener = loadMoreListener
    }

    public fun interface PinnedMessageSelectedListener {
        public fun onPinnedMessageSelected(message: Message)
    }

    public fun interface LoadMoreListener {
        public fun onLoadMoreRequested()
    }
}
