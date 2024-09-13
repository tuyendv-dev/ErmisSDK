package network.ermis.ui.view.messages.reactions.user

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.recyclerview.widget.GridLayoutManager
import network.ermis.core.models.Message
import network.ermis.core.models.Reaction
import network.ermis.core.models.User
import network.ermis.ui.ChatUI
import network.ermis.ui.components.R
import network.ermis.ui.components.databinding.UserReactionsViewBinding
import network.ermis.ui.common.state.messages.list.getUserReactionAlignment
import network.ermis.ui.view.messages.MessageListViewStyle
import network.ermis.ui.font.setTextStyle
import network.ermis.ui.utils.extensions.createStreamThemeWrapper
import network.ermis.ui.utils.extensions.streamThemeInflater
import network.ermis.ui.utils.extensions.supportedLatestReactions
import network.ermis.ui.utils.extensions.supportedReactionCounts

public class UserReactionsView : FrameLayout {

    private val binding = UserReactionsViewBinding.inflate(streamThemeInflater, this, true)

    private val userReactionsAdapter: UserReactionAdapter = UserReactionAdapter {
        userReactionClickListener?.onUserReactionClick(it.user, it.reaction)
    }
    private val gridLayoutManager: GridLayoutManager

    private var userReactionClickListener: UserReactionClickListener? = null

    public constructor(context: Context) : this(context, null, 0)
    public constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    public constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context.createStreamThemeWrapper(),
        attrs,
        defStyleAttr,
    )

    init {
        binding.recyclerView.adapter = userReactionsAdapter
        gridLayoutManager = binding.recyclerView.layoutManager as GridLayoutManager
    }

    public fun setMessage(message: Message, currentUser: User) {
        bindTitle(message)
        bindReactionList(message, currentUser)
    }

    internal fun setOnUserReactionClickListener(userReactionClickListener: UserReactionClickListener) {
        this.userReactionClickListener = userReactionClickListener
    }

    internal fun configure(messageListViewStyle: MessageListViewStyle) {
        binding.userReactionsContainer.setCardBackgroundColor(messageListViewStyle.userReactionsBackgroundColor)
        binding.userReactionsTitleTextView.setTextStyle(messageListViewStyle.userReactionsTitleText)
        userReactionsAdapter.messageOptionsUserReactionAlignment = messageListViewStyle
            .messageOptionsUserReactionAlignment.getUserReactionAlignment()
    }

    private fun bindTitle(message: Message) {
        val reactionCount = message.supportedLatestReactions.size
        binding.userReactionsTitleTextView.text = context.resources.getQuantityString(
            R.plurals.ermis_ui_message_list_message_reactions,
            reactionCount,
            reactionCount,
        )
    }

    private fun bindReactionList(message: Message, currentUser: User) {
        val userReactionItems = message.supportedLatestReactions.mapNotNull {
            val user = it.user
            val reactionDrawable = ChatUI.supportedReactions.getReactionDrawable(it.type)
            if (user != null && reactionDrawable != null) {
                UserReactionItem(
                    user = user,
                    reaction = it,
                    isMine = user.id == currentUser.id,
                    icon = reactionDrawable,
                    score = message.supportedReactionCounts[it.type] ?: 0
                )
            } else {
                null
            }
        }

        gridLayoutManager.spanCount = userReactionItems.size.coerceAtMost(MAX_COLUMNS_COUNT)
        userReactionsAdapter.submitList(userReactionItems)
    }

    internal fun interface UserReactionClickListener {
        fun onUserReactionClick(user: User, reaction: Reaction)
    }

    private companion object {
        private const val MAX_COLUMNS_COUNT = 4
    }
}
