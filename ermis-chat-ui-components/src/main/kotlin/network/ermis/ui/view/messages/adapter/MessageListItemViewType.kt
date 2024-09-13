
package network.ermis.ui.view.messages.adapter

/**
 * View type constants used by [MessageListItemViewHolderFactory].
 */
public object MessageListItemViewType {
    // A base offset to avoid clashes between built-in and custom view types
    private const val OFFSET = 1000

    public const val DATE_DIVIDER: Int = OFFSET + 1
    public const val MESSAGE_DELETED: Int = OFFSET + 2
    public const val PLAIN_TEXT: Int = OFFSET + 3
    public const val CUSTOM_ATTACHMENTS: Int = OFFSET + 4
    public const val LOADING_INDICATOR: Int = OFFSET + 5
    public const val THREAD_SEPARATOR: Int = OFFSET + 6
    public const val TYPING_INDICATOR: Int = OFFSET + 7
    public const val GIPHY: Int = OFFSET + 8
    public const val SYSTEM_MESSAGE: Int = OFFSET + 9
    public const val ERROR_MESSAGE: Int = OFFSET + 10
    public const val THREAD_PLACEHOLDER: Int = OFFSET + 11
    public const val GIPHY_ATTACHMENT: Int = OFFSET + 12
    public const val MEDIA_ATTACHMENT: Int = OFFSET + 13
    public const val FILE_ATTACHMENTS: Int = OFFSET + 14
    public const val LINK_ATTACHMENTS: Int = OFFSET + 15
    public const val UNREAD_SEPARATOR: Int = OFFSET + 16
    public const val START_OF_THE_CHANNEL: Int = OFFSET + 17
}
