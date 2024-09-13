
package network.ermis.ui.view.messages.options

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.TextView
import network.ermis.ui.components.R
import network.ermis.ui.common.state.messages.MessageAction
import network.ermis.ui.components.databinding.MessageOptionsViewBinding
import network.ermis.ui.view.MessageListView
import network.ermis.ui.view.messages.MessageListViewStyle
import network.ermis.ui.font.setTextStyle
import network.ermis.ui.utils.extensions.createStreamThemeWrapper
import network.ermis.ui.utils.extensions.inflater
import network.ermis.ui.utils.extensions.setStartDrawable
import network.ermis.ui.utils.extensions.streamThemeInflater

/**
 * Displays all available message actions a user can execute on a message.
 */
internal class MessageOptionsView : FrameLayout {

    private val binding = MessageOptionsViewBinding.inflate(streamThemeInflater, this, true)

    private var listener: MessageActionClickListener? = null

    constructor(context: Context) : this(context, null, 0)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context.createStreamThemeWrapper(),
        attrs,
        defStyleAttr,
    )

    /**
     * Sets a click listener for message option item clicks.
     *
     * @param listener The callback to be invoked when an option item is clicked.
     */
    fun setMessageActionClickListener(listener: MessageActionClickListener) {
        this.listener = listener
    }

    /**
     * Initializes the view with a set of message option items.
     *
     * @param messageOptions The list of message option items to display.
     * @param style Style for [MessageListView].
     */
    fun setMessageOptions(
        messageOptions: List<MessageOptionItem>,
        style: MessageListViewStyle,
    ) {
        binding.messageOptionsContainer.setCardBackgroundColor(style.messageOptionsBackgroundColor)

        binding.optionListContainer.removeAllViews()
        messageOptions.forEach { option ->
            val messageOptionTextView = inflater.inflate(
                R.layout.message_option_item,
                this,
                false,
            ) as TextView

            messageOptionTextView.text = option.optionText
            messageOptionTextView.setStartDrawable(option.optionIcon)
            messageOptionTextView.setOnClickListener {
                listener?.onMessageActionClick(option.messageAction)
            }

            val textStyle = if (option.isWarningItem) {
                style.warningMessageOptionsText
            } else {
                style.messageOptionsText
            }
            messageOptionTextView.setTextStyle(textStyle)

            binding.optionListContainer.addView(messageOptionTextView)
        }
    }

    /**
     * Click listener for message option items.
     */
    fun interface MessageActionClickListener {
        fun onMessageActionClick(messageAction: MessageAction)
    }
}
