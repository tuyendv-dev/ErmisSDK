
package network.ermis.ui.view.messages.preview

import network.ermis.ui.view.mentions.MentionListView
import network.ermis.ui.view.search.SearchResultListView
import network.ermis.ui.font.TextStyle

/**
 * Style for [MessagePreviewView] used by [MentionListView] and [SearchResultListView].
 *
 * @property messageSenderTextStyle Appearance for message sender text.
 * @property messageTextStyle Appearance for message text.
 * @property messageTimeTextStyle Appearance for message time text.
 */
public data class MessagePreviewStyle(
    val messageSenderTextStyle: TextStyle,
    val messageTextStyle: TextStyle,
    val messageTimeTextStyle: TextStyle,
)
