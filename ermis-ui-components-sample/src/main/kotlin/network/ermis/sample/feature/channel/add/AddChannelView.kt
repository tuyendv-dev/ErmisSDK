
package network.ermis.sample.feature.channel.add

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.core.content.res.use
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import network.ermis.core.models.User
import network.ermis.ui.view.messages.composer.MessageComposerView
import network.ermis.ui.view.MessageListView
import network.ermis.chat.ui.sample.R
import network.ermis.chat.ui.sample.databinding.AddChannelEmptyMessageListViewBinding
import network.ermis.chat.ui.sample.databinding.AddChannelViewBinding
import network.ermis.sample.feature.channel.add.AddChannelView.EndReachedListener

class AddChannelView : FrameLayout {

    private val binding = AddChannelViewBinding.inflate(LayoutInflater.from(context), this, true)
    private lateinit var controller: AddChannelViewController
    private var loadingView: View = defaultLoadingView()
    var endReachedListener: EndReachedListener = EndReachedListener { }
    private val endReachedScrollListener = EndReachedScrollListener()
    private var paginationEnabled: Boolean = false

    val messageListView: MessageListView
        get() = binding.messageListView

    val messageComposerView: MessageComposerView
        get() = binding.messageComposerView

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr,
    ) {
        init(attrs)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (binding.messageComposerView.isVisible && ev.action == MotionEvent.ACTION_DOWN) {
            val messageComposerViewRect = Rect().apply {
                binding.messageComposerView.getHitRect(this)
            }
            if (messageComposerViewRect.contains(ev.x.toInt(), ev.y.toInt())) {
                controller.messageComposerViewClicked()
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    private fun init(attrs: AttributeSet?) {
        context.obtainStyledAttributes(attrs, R.styleable.AddChannelView).use {
            val isAddGroupChannel = it.getBoolean(R.styleable.AddChannelView_isAddGroupChannel, false)

            binding.createGroupContainer.isVisible = !isAddGroupChannel
            binding.headerView.isVisible = !isAddGroupChannel
            binding.groupHeaderView.isVisible = isAddGroupChannel

            controller = AddChannelViewController(
                if (isAddGroupChannel) binding.groupHeaderView else binding.headerView,
                binding.usersTitle,
                binding.usersRecyclerView,
                binding.createGroupContainer,
                binding.messageListView,
                binding.messageComposerView,
                binding.emptyStateView,
                loadingView,
                isAddGroupChannel = isAddGroupChannel,
            )
        }

        binding.usersRecyclerView.addOnScrollListener(endReachedScrollListener)
        binding.usersRecyclerView.itemAnimator = null
        loadingView.apply {
            isVisible = false
            addView(this, defaultChildLayoutParams())
        }
        binding.messageListView.setEmptyStateView(
            AddChannelEmptyMessageListViewBinding.inflate(
                LayoutInflater.from(
                    context,
                ),
            ).root,
        )
    }

    fun setUsers(users: List<User>) {
        controller.setUsers(users)
    }

    fun addMoreUsers(users: List<User>) {
        controller.addMoreUsers(users)
    }

    fun setMembers(members: List<User>) {
        controller.setMembers(members)
    }

    fun setOnCreateGroupButtonListener(listener: CreateGroupButtonClickListener) {
        binding.createGroupContainer.setOnClickListener { listener.onButtonClick() }
    }

    fun showLoadingView() {
        loadingView.isVisible = true
        binding.usersRecyclerView.isVisible = false
        binding.emptyStateView.isVisible = false
    }

    fun setPaginationEnabled(enabled: Boolean) {
        paginationEnabled = enabled
    }

    fun setMembersChangedListener(listener: MembersChangedListener) {
        controller.membersChangedListener = listener
    }

    fun setSearchInputChangedListener(listener: SearchInputChangedListener) {
        controller.searchInputChangedListener = listener
    }

    fun interface EndReachedListener {
        fun onEndReached()
    }

    fun interface CreateGroupButtonClickListener {
        fun onButtonClick()
    }

    fun interface MembersChangedListener {
        fun onMembersChanged(members: List<User>)
    }

    fun interface SearchInputChangedListener {
        fun onInputChanged(query: String)
    }

    private fun defaultLoadingView(): View = ProgressBar(context)

    private fun defaultChildLayoutParams() =
        LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER)

    private inner class EndReachedScrollListener : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (RecyclerView.SCROLL_STATE_IDLE == newState) {
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisiblePosition = linearLayoutManager.findLastVisibleItemPosition()
                val reachedTheEnd = recyclerView.adapter!!.itemCount - 1 == lastVisiblePosition
                if (reachedTheEnd && paginationEnabled) {
                    endReachedListener.onEndReached()
                }
            }
        }
    }
}
