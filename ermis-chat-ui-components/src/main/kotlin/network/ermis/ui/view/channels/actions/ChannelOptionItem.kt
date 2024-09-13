
package network.ermis.ui.view.channels.actions

import android.graphics.drawable.Drawable
import network.ermis.ui.common.state.channels.ChannelAction

/**
 * UI representation of a Channel option, when the user selects a channel in the list.
 *
 * @param optionText The text of the option item.
 * @param optionIcon The icon of the option item.
 * @param channelAction The [ChannelAction] the option represents.
 * @param isWarningItem If the option item is dangerous.
 */
internal data class ChannelOptionItem(
    val optionIcon: Drawable,
    val optionText: String,
    val channelAction: ChannelAction,
    val isWarningItem: Boolean = false,
)
