package network.ermis.ui.view.messages.reactions.user

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.view.isVisible
import network.ermis.ui.components.databinding.ItemMessageReactionBinding
import network.ermis.ui.view.messages.reactions.view.MessageOptionsUserReactionOrientation
import network.ermis.ui.view.messages.reactions.view.getUserReactionOrientation
import network.ermis.ui.view.messages.reactions.view.isOrientedTowardsStart
import network.ermis.ui.utils.extensions.createStreamThemeWrapper
import network.ermis.ui.utils.extensions.streamThemeInflater

internal class SingleReactionView : FrameLayout {
    private val binding = ItemMessageReactionBinding.inflate(streamThemeInflater, this, true)
    private lateinit var reactionsViewStyle: SingleReactionViewStyle
    private lateinit var bubbleDrawer: SingleReactionViewBubbleDrawer
    private var isMyMessage: Boolean = false
    private val messageOrientation: MessageOptionsUserReactionOrientation
        get() = reactionsViewStyle.reactionOrientation.getUserReactionOrientation()

    constructor(context: Context) : super(context.createStreamThemeWrapper()) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context.createStreamThemeWrapper(), attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context.createStreamThemeWrapper(),
        attrs,
        defStyleAttr,
    ) {
        init(context, attrs)
    }

    fun setReaction(userReactionItem: UserReactionItem) {
        // according to the design, current user reactions have the same style
        // as reactions on the current user messages in the message list
        this.isMyMessage = userReactionItem.isMine
        binding.reactionIcon.text = userReactionItem.icon
        binding.tvScoreReaction.isVisible = false
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val isOrientedTowardsStart = messageOrientation.isOrientedTowardsStart(isMyMessage)

        bubbleDrawer.drawReactionsBubble(
            context = context,
            canvas = canvas,
            bubbleWidth = width,
            isMyMessage = isMyMessage,
            isOrientedTowardsStart = isOrientedTowardsStart,
        )
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        this.reactionsViewStyle = SingleReactionViewStyle(context, attrs)
        this.bubbleDrawer = SingleReactionViewBubbleDrawer(reactionsViewStyle)

        setWillNotDraw(false)
        minimumHeight = reactionsViewStyle.totalHeight
    }
}
