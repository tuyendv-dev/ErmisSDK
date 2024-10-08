
package network.ermis.ui.view.messages.internal

import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import network.ermis.ui.common.utils.Utils
import network.ermis.ui.view.messages.internal.LongClickFriendlyLinkMovementMethod.Companion.set
import network.ermis.ui.utils.shouldConsumeLongTap

/**
 * A customized [LinkMovementMethod] implementation that lets you handle links being
 * clicked with [onLinkClicked], while also keeping long clicks as a separate event,
 * forwarded to [longClickTarget].
 *
 * This class is reattached to the target [textView] every time its content changes,
 * so you only need to call [set] one time.
 */
internal class LongClickFriendlyLinkMovementMethod private constructor(
    private val textView: TextView,
    private val longClickTarget: View,
    private val onLinkClicked: (url: String) -> Unit,
) : Utils.TextViewLinkHandler() {
    private var isLongClick = false

    init {
        /** [shouldConsumeLongTap] check fixes issue https://github.com/GetStream/stream-chat-android/issues/3255
         * return false as before for other manufacturers
         */
        textView.setOnLongClickListener {
            isLongClick = true
            longClickTarget.performLongClick()
            shouldConsumeLongTap()
        }
        textView.doAfterTextChanged {
            textView.movementMethod = this
        }
    }

    override fun onLinkClick(url: String) {
        if (isLongClick) {
            isLongClick = false
            return
        }
        onLinkClicked(url)
    }

    companion object {
        fun set(
            textView: TextView,
            longClickTarget: View,
            onLinkClicked: (url: String) -> Unit,
        ) {
            LongClickFriendlyLinkMovementMethod(textView, longClickTarget, onLinkClicked)
        }
    }
}
