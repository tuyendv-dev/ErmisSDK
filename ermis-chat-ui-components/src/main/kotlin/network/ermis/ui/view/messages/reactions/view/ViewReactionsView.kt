
package network.ermis.ui.view.messages.reactions.view

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import network.ermis.core.models.Message
import network.ermis.ui.ChatUI
import network.ermis.ui.view.messages.reactions.ReactionClickListener
import network.ermis.ui.view.messages.reactions.ReactionItem
import network.ermis.ui.view.messages.reactions.ReactionsAdapter
import network.ermis.ui.utils.extensions.createStreamThemeWrapper
import network.ermis.ui.utils.extensions.hasSingleReaction
import network.ermis.ui.utils.extensions.supportedReactionCounts

public class ViewReactionsView : RecyclerView {

    private lateinit var reactionsViewStyle: ViewReactionsViewStyle
    private lateinit var reactionsAdapter: ReactionsAdapter
    private lateinit var bubbleDrawer: ViewReactionsBubbleDrawer

    private var reactionClickListener: ReactionClickListener? = null
    private var isMyMessage: Boolean = false
    private var isSingleReaction: Boolean = true

    public constructor(context: Context) : super(context.createStreamThemeWrapper()) {
        init(context, null)
    }

    public constructor(context: Context, attrs: AttributeSet?) : super(context.createStreamThemeWrapper(), attrs) {
        init(context, attrs)
    }

    public constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context.createStreamThemeWrapper(),
        attrs,
        defStyleAttr,
    ) {
        init(context, attrs)
    }

    public fun setMessage(message: Message, isMyMessage: Boolean, commitCallback: () -> Unit = {}) {
        this.isMyMessage = isMyMessage
        this.isSingleReaction = message.hasSingleReaction()

        reactionsAdapter.submitList(createReactionItems(message)) {
            setPadding(
                reactionsViewStyle.horizontalPadding,
                reactionsViewStyle.verticalPadding,
                reactionsViewStyle.horizontalPadding,
                reactionsViewStyle.verticalPadding,
            )

            commitCallback()
        }
    }

    public fun setReactionClickListener(reactionClickListener: ReactionClickListener) {
        this.reactionClickListener = reactionClickListener
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        bubbleDrawer.drawReactionsBubble(context, canvas, width, isMyMessage, isSingleReaction)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        applyStyle(ViewReactionsViewStyle(context, attrs))
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        itemAnimator = null
        overScrollMode = View.OVER_SCROLL_NEVER
        setWillNotDraw(false)
    }

    internal fun applyStyle(style: ViewReactionsViewStyle) {
        this.reactionsViewStyle = style
        this.bubbleDrawer = ViewReactionsBubbleDrawer(reactionsViewStyle)
        minimumHeight = reactionsViewStyle.totalHeight
        setPadding(
            reactionsViewStyle.horizontalPadding,
            reactionsViewStyle.verticalPadding,
            reactionsViewStyle.horizontalPadding,
            reactionsViewStyle.verticalPadding,
        )

        adapter = ReactionsAdapter(reactionsViewStyle.itemSize, true) {
            reactionClickListener?.onReactionClick(it)
        }.also { reactionsAdapter = it }
    }

    private fun createReactionItems(message: Message): List<ReactionItem> {
        val currentUserId = ChatUI.currentUserProvider.getCurrentUser()?.id
        return message.supportedReactionCounts.keys
            .mapNotNull { type ->
                ChatUI.supportedReactions.getReactionDrawable(type)?.let {
                    ReactionItem(
                        type = type,
                        isMine = message.latestReactions.any { it.type == type && it.userId == currentUserId},
                        icon = it,
                        score = message.supportedReactionCounts[type] ?: 0
                    )
                }
            }.sortedBy { if (isMyMessage) it.isMine else !it.isMine }
    }
}
