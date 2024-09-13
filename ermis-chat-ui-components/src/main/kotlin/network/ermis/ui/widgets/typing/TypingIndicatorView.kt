
package network.ermis.ui.widgets.typing

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import network.ermis.core.models.User
import network.ermis.ui.components.R
import network.ermis.ui.font.setTextStyle
import network.ermis.ui.utils.extensions.createStreamThemeWrapper
import network.ermis.ui.utils.extensions.dpToPx
import network.ermis.ui.utils.extensions.streamThemeInflater

public class TypingIndicatorView : LinearLayout {

    public constructor(context: Context) : super(context.createStreamThemeWrapper()) {
        init(null)
    }

    public constructor(context: Context, attrs: AttributeSet?) : super(context.createStreamThemeWrapper(), attrs) {
        init(attrs)
    }

    public constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context.createStreamThemeWrapper(),
        attrs,
        defStyleAttr,
    ) {
        init(attrs)
    }

    private val userTypingTextView: TextView = TextView(context)
    private lateinit var style: TypingIndicatorViewStyle

    private fun init(attrs: AttributeSet?) {
        val horizontalPadding = 8.dpToPx()
        setPadding(horizontalPadding, 0, horizontalPadding, 0)
        gravity = Gravity.CENTER_VERTICAL
        orientation = HORIZONTAL
        isVisible = false

        style = TypingIndicatorViewStyle(context, attrs)

        streamThemeInflater.inflate(style.typingIndicatorAnimationView, this)

        addView(
            userTypingTextView.apply { setTextStyle(style.typingIndicatorUsersTextStyle) },
            LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                marginStart = 8.dpToPx()
            },
        )
    }

    public fun setTypingUsers(users: List<User>) {
        isVisible = if (users.isEmpty()) {
            false
        } else {
            when (users.size) {
                1 -> userTypingTextView.text = resources.getString(R.string.ermis_ui_message_list_header_typing_user, users.first().name)
                2 -> userTypingTextView.text = resources.getString(
                    R.string.ermis_ui_message_list_header_typing_user_two,
                    users.first().name,
                    users.size - 1,
                )
                else -> userTypingTextView.text = resources.getString(
                    R.string.ermis_ui_message_list_header_typing_users,
                    users.first().name,
                    users.size - 1,
                )
            }
            true
        }
    }
}
