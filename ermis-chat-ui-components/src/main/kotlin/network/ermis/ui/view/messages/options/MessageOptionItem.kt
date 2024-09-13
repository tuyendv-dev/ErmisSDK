
package network.ermis.ui.view.messages.options

import android.graphics.drawable.Drawable
import network.ermis.ui.common.state.messages.MessageAction

/**
 * UI representation of a Message option, when the user selects a message in the list.
 *
 * @param optionText The text of the option item.
 * @param optionIcon The icon of the option item.
 * @param messageAction The [MessageAction] the option represents.
 * @param isWarningItem If the option item is dangerous.
 */
public data class MessageOptionItem(
    public val optionText: String,
    public val optionIcon: Drawable,
    public val messageAction: MessageAction,
    public val isWarningItem: Boolean = false,
)
