package network.ermis.ui.view.messages.reactions.edit

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import network.ermis.ui.ChatUI
import network.ermis.ui.view.messages.reactions.ReactionClickListener
import network.ermis.ui.view.messages.reactions.ReactionItem
import network.ermis.ui.view.messages.reactions.ReactionsAdapter
import network.ermis.ui.utils.extensions.createStreamThemeWrapper
import network.ermis.ui.utils.extensions.dpToPx
import network.ermis.ui.utils.extensions.supportedReactionCounts
import network.ermis.core.models.Message
import kotlin.math.ceil

private const val TAIL_BUBBLE_SPACE_DP = 16

public class EditReactionsView : RecyclerView {

    private lateinit var reactionsViewStyle: EditReactionsViewStyle
    private lateinit var reactionsAdapter: ReactionsAdapter
    private lateinit var bubbleDrawer: EditReactionsBubbleDrawer

    private var reactionClickListener: ReactionClickListener? = null
    private var isMyMessage: Boolean = false
    private var messageAnchorPosition: Float = 0f

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

    private var bubbleHeight: Int = 0
    private var reactionsColumns: Int = 5

    public fun setMessage(message: Message, isMyMessage: Boolean) {
        this.isMyMessage = isMyMessage
        val currentUserId = ChatUI.currentUserProvider.getCurrentUser()?.id
        val reactionItems: List<ReactionItem> = ChatUI.supportedReactions.reactions.map { (type, icon) ->
            ReactionItem(
                type = type,
                isMine = message.latestReactions.any { it.type == type && it.userId == currentUserId },
                icon = icon,
                score = message.supportedReactionCounts[type] ?: 0
            )
        }

        if (reactionItems.size > reactionsColumns) {
            val timesBigger = ceil(reactionItems.size.toFloat() / reactionsColumns).toInt()
            bubbleHeight = bubbleHeight.times(timesBigger)
        }

        minimumHeight = bubbleHeight + TAIL_BUBBLE_SPACE_DP.dpToPx()

        reactionsAdapter.submitList(reactionItems)
    }

    public fun setReactionClickListener(reactionClickListener: ReactionClickListener) {
        this.reactionClickListener = reactionClickListener
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val canvasWidth = width
        val bubbleDrawOffset = 12.dpToPx()
        val boundsEnd = canvasWidth - bubbleDrawOffset

        val canvasBounds = bubbleDrawOffset..boundsEnd

        bubbleDrawer.drawReactionsBubble(
            context = context,
            canvas = canvas,
            bubbleWidth = width,
            bubbleHeight = bubbleHeight,
            isMyMessage = isMyMessage,
            isSingleReaction = true,
            messageAnchorPosition = messageAnchorPosition,
            canvasBounds = canvasBounds,
        )
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        val style = EditReactionsViewStyle(context, attrs)
        applyStyle(style)

        bubbleHeight = style.bubbleHeight

        itemAnimator = null
        overScrollMode = OVER_SCROLL_NEVER
        setWillNotDraw(false)
    }

    internal fun applyStyle(editReactionsViewStyle: EditReactionsViewStyle) {
        this.reactionsViewStyle = editReactionsViewStyle
        this.bubbleDrawer = EditReactionsBubbleDrawer(reactionsViewStyle)

        reactionsColumns = minOf(ChatUI.supportedReactions.reactions.size, editReactionsViewStyle.reactionsColumn)
        setPadding(
            reactionsViewStyle.horizontalPadding,
            reactionsViewStyle.verticalPadding,
            reactionsViewStyle.horizontalPadding,
            reactionsViewStyle.verticalPadding,
        )

        layoutManager = GridLayoutManager(context, reactionsColumns)

        adapter = ReactionsAdapter(reactionsViewStyle.itemSize, false) {
            reactionClickListener?.onReactionClick(it)
        }.also { reactionsAdapter = it }
    }

    internal fun positionBubbleTail(messageAnchorPosition: Float) {
        this.messageAnchorPosition = messageAnchorPosition

        requestLayout()
    }
}