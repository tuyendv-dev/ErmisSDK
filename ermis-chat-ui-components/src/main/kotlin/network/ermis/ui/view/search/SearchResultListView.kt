
package network.ermis.ui.view.search

import android.content.Context
import android.util.AttributeSet
import android.widget.Toast
import android.widget.ViewFlipper
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import network.ermis.core.models.Message
import network.ermis.ui.components.R
import network.ermis.ui.components.databinding.SearchResultListViewBinding
import network.ermis.ui.font.setTextStyle
import network.ermis.ui.utils.extensions.createStreamThemeWrapper
import network.ermis.ui.utils.extensions.streamThemeInflater
import network.ermis.ui.widgets.EndlessScrollListener

/**
 * View used to display messages that contain specific text.
 */
public class SearchResultListView : ViewFlipper {

    private val binding = SearchResultListViewBinding.inflate(streamThemeInflater, this)

    /**
     * Callback invoked when we've reached the end of messages.
     */
    private var loadMoreListener: LoadMoreListener? = null

    private val adapter = SearchResultListAdapter()

    private val scrollListener = EndlessScrollListener(LOAD_MORE_THRESHOLD) {
        loadMoreListener?.onLoadMoreRequested()
    }

    public constructor(context: Context) : this(context, null)

    public constructor(context: Context, attrs: AttributeSet?) : super(context.createStreamThemeWrapper(), attrs) {
        setupViewStyle(attrs)
        setupView()
    }

    private fun setupViewStyle(attrs: AttributeSet?) {
        SearchResultListViewStyle(context, attrs).also { style ->
            setBackgroundColor(style.backgroundColor)
            binding.searchInfoBar.background = style.searchInfoBarBackground
            binding.searchInfoBar.setTextStyle(style.searchInfoBarTextStyle)
            binding.emptyImage.setImageDrawable(style.emptyStateIcon)
            binding.emptyLabel.setTextStyle(style.emptyStateTextStyle)
            binding.progressBar.indeterminateDrawable = style.progressBarIcon
            adapter.messagePreviewStyle = style.messagePreviewStyle
        }
    }

    private fun setupView() {
        binding.searchListView.apply {
            setHasFixedSize(true)
            adapter = this@SearchResultListView.adapter
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

    /**
     * Shows the list of search results.
     */
    public fun showMessages(query: String, messages: List<Message>) {
        val isEmpty = messages.isEmpty()

        displayedChild = if (isEmpty) Flipper.EMPTY else Flipper.RESULTS

        if (!isEmpty) {
            binding.searchInfoBar.text =
                resources.getQuantityString(R.plurals.ermis_ui_search_results_count, messages.size, messages.size)
        } else {
            binding.emptyLabel.text = context.getString(R.string.ermis_ui_search_results_empty, query)
        }

        adapter.submitList(messages)
    }

    /**
     * Shows a loading view during the initial load.
     */
    public fun showLoading() {
        displayedChild = Flipper.LOADING

        adapter.submitList(emptyList())
        scrollListener.disablePagination()
    }

    /**
     * Show a generic error message represented as a [Toast].
     */
    public fun showError() {
        Toast.makeText(context, R.string.ermis_ui_search_results_error, Toast.LENGTH_SHORT).show()
    }

    /**
     * Enabled or disables pagination. If pagination is disabled, the listener set in
     * [setLoadMoreListener] will not be triggered when scrolling to the end of the list.
     */
    public fun setPaginationEnabled(isEnabled: Boolean) {
        if (isEnabled) {
            scrollListener.enablePagination()
        } else {
            scrollListener.disablePagination()
        }
    }

    /**
     * Sets the listener to handle search result item clicks.
     */
    public fun setSearchResultSelectedListener(searchResultSelectedListener: SearchResultSelectedListener?) {
        adapter.setSearchResultSelectedListener(searchResultSelectedListener)
    }

    /**
     * Set the callback which is invoked we've reached the end of messages.
     */
    public fun setLoadMoreListener(loadMoreListener: LoadMoreListener?) {
        this.loadMoreListener = loadMoreListener
    }

    /**
     * Click listener for search result item clicks.
     */
    public fun interface SearchResultSelectedListener {
        public fun onSearchResultSelected(message: Message)
    }

    /**
     * Callback which is invoked we've reached the end of messages.
     */
    public fun interface LoadMoreListener {
        public fun onLoadMoreRequested()
    }

    private object Flipper {
        const val RESULTS = 0
        const val EMPTY = 1
        const val LOADING = 2
    }

    private companion object {
        const val LOAD_MORE_THRESHOLD = 10
    }
}
