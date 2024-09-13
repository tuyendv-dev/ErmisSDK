
package network.ermis.ui.view.messages

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.view.Gravity
import androidx.annotation.ColorInt
import androidx.annotation.LayoutRes
import androidx.annotation.Px
import androidx.annotation.StyleableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import network.ermis.ui.components.R
import network.ermis.ui.view.messages.MessageListItemStyle.Companion.MESSAGE_STROKE_COLOR_MINE
import network.ermis.ui.view.messages.MessageListItemStyle.Companion.MESSAGE_STROKE_COLOR_THEIRS
import network.ermis.ui.view.messages.MessageListItemStyle.Companion.MESSAGE_STROKE_WIDTH_MINE
import network.ermis.ui.view.messages.MessageListItemStyle.Companion.MESSAGE_STROKE_WIDTH_THEIRS
import network.ermis.ui.view.messages.reactions.edit.EditReactionsViewStyle
import network.ermis.ui.view.messages.reactions.edit.EditReactionsView
import network.ermis.ui.view.messages.reactions.view.ViewReactionsViewStyle
import network.ermis.ui.view.messages.reactions.view.ViewReactionsView
import network.ermis.ui.font.TextStyle
import network.ermis.ui.helper.TransformStyle
import network.ermis.ui.helper.ViewStyle
import network.ermis.ui.utils.extensions.dpToPxPrecise
import network.ermis.ui.utils.extensions.getColorCompat
import network.ermis.ui.utils.extensions.getDimension
import network.ermis.ui.utils.extensions.getDrawableCompat

/**
 * Style for view holders used inside [MessageListView].
 * Use this class together with [TransformStyle.messageListItemStyleTransformer] to change styles programmatically.
 *
 * @property messageBackgroundColorMine Background color for message sent by the current user. Default value is [R.color.ui_grey_gainsboro].
 * @property messageBackgroundColorTheirs Background color for message sent by other user. Default value is [R.color.ui_white].
 * @property messageLinkTextColorMine Color for links sent by the current user. Default value is [R.color.ui_accent_blue].
 * @property messageLinkTextColorTheirs Color for links sent by other user. Default value is [R.color.ui_accent_blue].
 * @property messageLinkBackgroundColorMine Background color for message with link, sent by the current user. Default value is [R.color.ui_blue_alice].
 * @property messageLinkBackgroundColorTheirs Background color for message with link, sent by other user. Default value is [R.color.ui_blue_alice].
 * @property linkDescriptionMaxLines Max lines for link's description. Default value is 5.
 * @property textStyleMine Appearance for message text sent by the current user.
 * @property textStyleTheirs Appearance for message text sent by other user.
 * @property textStyleUserName Appearance for user name text.
 * @property textStyleMessageDate Appearance for message date text.
 * @property textStyleMessageLanguage Appearance for message language text.
 * @property textStyleThreadCounter Appearance for thread counter text.
 * @property textStyleReadCounter Appearance for read counter text.
 * @property textStyleLinkTitle Appearance for link.
 * @property textStyleLinkDescription Appearance for link's description text.
 * @property dateSeparatorBackgroundColor Background color for data separator. Default value is [R.color.ui_overlay_dark].
 * @property textStyleDateSeparator Appearance for date separator text.
 * @property reactionsViewStyle Style for [ViewReactionsView].
 * @property editReactionsViewStyle Style for [EditReactionsView].
 * @property iconIndicatorSent Icon for message's sent status. Default value is [R.drawable.ic_check_single].
 * @property iconIndicatorRead Icon for message's read status. Default value is [R.drawable.ic_check_double].
 * @property iconIndicatorPendingSync Icon for message's pending status. Default value is [R.drawable.ic_clock].
 * @property iconOnlyVisibleToYou Icon for message's pending status. Default value is [R.drawable.ic_icon_eye_off].
 * @property textStyleMessageDeleted Appearance for message deleted text.
 * @property messageDeletedBackground Background color for deleted message. Default value is [R.color.ui_grey_whisper].
 * @property textStyleMessageDeletedMine Appearance for mine message deleted text. Default value is [textStyleMessageDeleted].
 * @property messageDeletedBackgroundMine Background color for mine deleted message. Default value is [messageDeletedBackground].
 * @property textStyleMessageDeletedTheirs Appearance for theirs message deleted text. Default value is [textStyleMessageDeleted].
 * @property messageDeletedBackgroundTheirs Background color for theirs deleted message. Default value is [messageDeletedBackground].
 * @property messageStrokeColorMine Stroke color for message sent by the current user. Default value is [MESSAGE_STROKE_COLOR_MINE].
 * @property messageStrokeWidthMine Stroke width for message sent by the current user. Default value is [MESSAGE_STROKE_WIDTH_MINE].
 * @property messageStrokeColorTheirs Stroke color for message sent by other user. Default value is [MESSAGE_STROKE_COLOR_THEIRS].
 * @property messageStrokeWidthTheirs Stroke width for message sent by other user. Default value is [MESSAGE_STROKE_WIDTH_THEIRS].
 * @property textStyleSystemMessage Appearance for system message text.
 * @property textStyleErrorMessage Appearance for error message text.
 * @property messageStartMargin Margin for messages in the left side. Default value is 48dp.
 * @property messageEndMargin Margin for messages in the right side. Default value is 0dp.
 * @property messageMaxWidthFactorMine Factor used to compute max width for message sent by the current user. Should be in <0.75, 1> range.
 * @property messageMaxWidthFactorTheirs Factor used to compute max width for message sent by other user. Should be in <0.75, 1> range.
 * @property showMessageDeliveryStatusIndicator Flag if we need to show the delivery indicator or not.
 * @property iconFailedMessage Icon for message failed status. Default value is [R.drawable.ic_warning].
 * @property iconBannedMessage Icon for message when the current user is banned. Default value is [R.drawable.ic_warning].
 * @property systemMessageAlignment Changes the alignment of system messages.
 * @property loadingMoreView Loading more view. Default value is [R.layout.message_list_loading_more_view].
 */
public data class MessageListItemStyle(
    @ColorInt public val messageBackgroundColorMine: Int?,
    @ColorInt public val messageBackgroundColorTheirs: Int?,
    @ColorInt public val messageLinkTextColorMine: Int?,
    @ColorInt public val messageLinkTextColorTheirs: Int?,
    @ColorInt public val messageLinkBackgroundColorMine: Int,
    @ColorInt public val messageLinkBackgroundColorTheirs: Int,
    public val linkDescriptionMaxLines: Int,
    public val textStyleMine: TextStyle,
    public val textStyleTheirs: TextStyle,
    public val textStyleUserName: TextStyle,
    public val textStyleMessageDate: TextStyle,
    public val textStyleMessageLanguage: TextStyle,
    public val textStyleThreadCounter: TextStyle,
    public val textStyleReadCounter: TextStyle,
    public val threadSeparatorTextStyle: TextStyle,
    public val textStyleLinkLabel: TextStyle,
    public val textStyleLinkTitle: TextStyle,
    public val textStyleLinkDescription: TextStyle,
    @ColorInt public val dateSeparatorBackgroundColor: Int,
    public val textStyleDateSeparator: TextStyle,
    public val reactionsViewStyle: ViewReactionsViewStyle,
    public val editReactionsViewStyle: EditReactionsViewStyle,
    public val iconIndicatorSent: Drawable,
    public val iconIndicatorRead: Drawable,
    public val iconIndicatorPendingSync: Drawable,
    public val iconOnlyVisibleToYou: Drawable,
    @Deprecated(
        message = "Use textStyleMessageDeletedMine and textStyleMessageDeletedTheirs instead.",
        replaceWith = ReplaceWith("textStyleMessageDeletedMine and textStyleMessageDeletedTheirs"),
        level = DeprecationLevel.WARNING,
    )
    public val textStyleMessageDeleted: TextStyle,
    @Deprecated(
        message = "Use messageDeletedBackgroundMine and messageDeletedBackgroundTheirs instead.",
        replaceWith = ReplaceWith("messageDeletedBackgroundMine and messageDeletedBackgroundTheirs"),
        level = DeprecationLevel.WARNING,
    )
    @ColorInt public val messageDeletedBackground: Int,
    public val textStyleMessageDeletedMine: TextStyle?,
    @ColorInt public val messageDeletedBackgroundMine: Int?,
    public val textStyleMessageDeletedTheirs: TextStyle?,
    @ColorInt public val messageDeletedBackgroundTheirs: Int?,
    @ColorInt public val messageStrokeColorMine: Int,
    @Px public val messageStrokeWidthMine: Float,
    @ColorInt public val messageStrokeColorTheirs: Int,
    @Px public val messageStrokeWidthTheirs: Float,
    public val textStyleSystemMessage: TextStyle,
    public val textStyleErrorMessage: TextStyle,
    public val pinnedMessageIndicatorTextStyle: TextStyle,
    public val pinnedMessageIndicatorIcon: Drawable,
    @ColorInt public val pinnedMessageBackgroundColor: Int,
    @Px public val messageStartMargin: Int,
    @Px public val messageEndMargin: Int,
    public val messageMaxWidthFactorMine: Float,
    public val messageMaxWidthFactorTheirs: Float,
    public val showMessageDeliveryStatusIndicator: Boolean,
    public val iconFailedMessage: Drawable,
    public val iconBannedMessage: Drawable,
    public val systemMessageAlignment: Int,
    @LayoutRes public val loadingMoreView: Int,
    @ColorInt public val unreadSeparatorBackgroundColor: Int,
    public val unreadSeparatorTextStyle: TextStyle,
) : ViewStyle {

    @ColorInt
    public fun getStyleTextColor(isMine: Boolean): Int? {
        return if (isMine) textStyleMine.colorOrNull() else textStyleTheirs.colorOrNull()
    }

    @ColorInt
    public fun getStyleLinkTextColor(isMine: Boolean): Int? {
        return if (isMine) messageLinkTextColorMine else messageLinkTextColorTheirs
    }

    internal companion object {
        internal const val VALUE_NOT_SET = Integer.MAX_VALUE

        internal val DEFAULT_LINK_BACKGROUND_COLOR = R.color.ui_blue_alice

        internal val DEFAULT_TEXT_COLOR = R.color.ui_text_color_primary
        internal val DEFAULT_TEXT_SIZE = R.dimen.ui_text_medium
        internal const val DEFAULT_TEXT_STYLE = Typeface.NORMAL

        internal val DEFAULT_TEXT_COLOR_USER_NAME = R.color.ui_text_color_secondary
        internal val DEFAULT_TEXT_SIZE_USER_NAME = R.dimen.ui_text_small

        internal val DEFAULT_TEXT_COLOR_DATE = R.color.ui_text_color_secondary
        internal val DEFAULT_TEXT_SIZE_DATE = R.dimen.ui_text_small

        internal val DEFAULT_TEXT_COLOR_THREAD_COUNTER = R.color.ui_accent_blue
        internal val DEFAULT_TEXT_SIZE_THREAD_COUNTER = R.dimen.ui_text_small

        internal val DEFAULT_TEXT_COLOR_READ_COUNTER = R.color.ui_text_color_secondary
        internal val DEFAULT_TEXT_SIZE_READ_COUNTER = R.dimen.ui_text_small

        internal val DEFAULT_TEXT_COLOR_LINK_DESCRIPTION = R.color.ui_text_color_secondary
        internal val DEFAULT_TEXT_SIZE_LINK_DESCRIPTION = R.dimen.ui_text_small

        internal val DEFAULT_TEXT_COLOR_DATE_SEPARATOR = R.color.ui_white
        internal val DEFAULT_TEXT_SIZE_DATE_SEPARATOR = R.dimen.ui_text_small

        internal val MESSAGE_STROKE_COLOR_MINE = R.color.ui_literal_transparent
        internal const val MESSAGE_STROKE_WIDTH_MINE: Float = 0f
        internal val MESSAGE_STROKE_COLOR_THEIRS = R.color.ui_grey_whisper
        internal val MESSAGE_STROKE_WIDTH_THEIRS: Float = 1.dpToPxPrecise()

        private const val BASE_MESSAGE_MAX_WIDTH_FACTOR = 1
        private const val DEFAULT_MESSAGE_MAX_WIDTH_FACTOR = 0.75f
    }

    internal class Builder(private val attributes: TypedArray, private val context: Context) {
        @ColorInt
        private var messageBackgroundColorMine: Int = VALUE_NOT_SET

        @ColorInt
        private var messageBackgroundColorTheirs: Int = VALUE_NOT_SET

        @ColorInt
        private var messageLinkTextColorMine: Int = VALUE_NOT_SET

        @ColorInt
        private var messageLinkTextColorTheirs: Int = VALUE_NOT_SET

        private var reactionsEnabled: Boolean = true

        private var linkDescriptionMaxLines: Int = 5

        private var systemMessageGravity: Int = Gravity.CENTER

        fun messageBackgroundColorMine(
            @StyleableRes messageBackgroundColorMineStyleableId: Int,
            @ColorInt defaultValue: Int = VALUE_NOT_SET,
        ) = apply {
            messageBackgroundColorMine = attributes.getColor(messageBackgroundColorMineStyleableId, defaultValue)
        }

        fun messageBackgroundColorTheirs(
            @StyleableRes messageBackgroundColorTheirsId: Int,
            @ColorInt defaultValue: Int = VALUE_NOT_SET,
        ) = apply {
            messageBackgroundColorTheirs = attributes.getColor(messageBackgroundColorTheirsId, defaultValue)
        }

        fun messageLinkTextColorMine(
            @StyleableRes messageLinkTextColorMineId: Int,
            @ColorInt defaultValue: Int = VALUE_NOT_SET,
        ) = apply {
            messageLinkTextColorMine = attributes.getColor(messageLinkTextColorMineId, defaultValue)
        }

        fun messageLinkTextColorTheirs(
            @StyleableRes messageLinkTextColorTheirsId: Int,
            @ColorInt defaultValue: Int = VALUE_NOT_SET,
        ) = apply {
            messageLinkTextColorTheirs = attributes.getColor(messageLinkTextColorTheirsId, defaultValue)
        }

        fun reactionsEnabled(
            @StyleableRes reactionsEnabled: Int,
            defaultValue: Boolean = true,
        ) = apply {
            this.reactionsEnabled = attributes.getBoolean(reactionsEnabled, defaultValue)
        }

        fun linkDescriptionMaxLines(
            maxLines: Int,
            defaultValue: Int = 5,
        ) = apply {
            this.linkDescriptionMaxLines = attributes.getInt(maxLines, defaultValue)
        }

        fun systemMessageGravity(
            @StyleableRes systemMessageGravity: Int,
            defaultGravity: Int = Gravity.CENTER,
        ) = apply {
            this.systemMessageGravity = attributes.getInt(systemMessageGravity, defaultGravity)
        }

        fun build(): MessageListItemStyle {
            val linkBackgroundColorMine =
                attributes.getColor(
                    R.styleable.MessageListView_ermisUiMessageLinkBackgroundColorMine,
                    context.getColorCompat(DEFAULT_LINK_BACKGROUND_COLOR),
                )
            val linkBackgroundColorTheirs =
                attributes.getColor(
                    R.styleable.MessageListView_ermisUiMessageLinkBackgroundColorTheirs,
                    context.getColorCompat(DEFAULT_LINK_BACKGROUND_COLOR),
                )

            val mediumTypeface = ResourcesCompat.getFont(context, R.font.roboto_medium) ?: Typeface.DEFAULT
            val boldTypeface = ResourcesCompat.getFont(context, R.font.roboto_bold) ?: Typeface.DEFAULT_BOLD

            val textStyleMine = TextStyle.Builder(attributes)
                .size(
                    R.styleable.MessageListView_ermisUiMessageTextSizeMine,
                    context.getDimension(DEFAULT_TEXT_SIZE),
                )
                .color(
                    R.styleable.MessageListView_ermisUiMessageTextColorMine,
                    context.getColorCompat(DEFAULT_TEXT_COLOR),
                )
                .font(
                    R.styleable.MessageListView_ermisUiMessageTextFontAssetsMine,
                    R.styleable.MessageListView_ermisUiMessageTextFontMine,
                    mediumTypeface,
                )
                .style(R.styleable.MessageListView_ermisUiMessageTextStyleMine, DEFAULT_TEXT_STYLE)
                .build()

            val textStyleTheirs = TextStyle.Builder(attributes)
                .size(
                    R.styleable.MessageListView_ermisUiMessageTextSizeTheirs,
                    context.getDimension(DEFAULT_TEXT_SIZE),
                )
                .color(
                    R.styleable.MessageListView_ermisUiMessageTextColorTheirs,
                    context.getColorCompat(DEFAULT_TEXT_COLOR),
                )
                .font(
                    R.styleable.MessageListView_ermisUiMessageTextFontAssetsTheirs,
                    R.styleable.MessageListView_ermisUiMessageTextFontTheirs,
                    mediumTypeface,
                )
                .style(R.styleable.MessageListView_ermisUiMessageTextStyleTheirs, DEFAULT_TEXT_STYLE)
                .build()

            val textStyleUserName = TextStyle.Builder(attributes)
                .size(
                    R.styleable.MessageListView_ermisUiMessageTextSizeUserName,
                    context.getDimension(DEFAULT_TEXT_SIZE_USER_NAME),
                )
                .color(
                    R.styleable.MessageListView_ermisUiMessageTextColorUserName,
                    context.getColorCompat(DEFAULT_TEXT_COLOR_USER_NAME),
                )
                .font(
                    R.styleable.MessageListView_ermisUiMessageTextFontAssetsUserName,
                    R.styleable.MessageListView_ermisUiMessageTextFontUserName,
                )
                .style(R.styleable.MessageListView_ermisUiMessageTextStyleUserName, DEFAULT_TEXT_STYLE)
                .build()

            val textStyleMessageDate = TextStyle.Builder(attributes)
                .size(
                    R.styleable.MessageListView_ermisUiMessageTextSizeDate,
                    context.getDimension(DEFAULT_TEXT_SIZE_DATE),
                )
                .color(
                    R.styleable.MessageListView_ermisUiMessageTextColorDate,
                    context.getColorCompat(DEFAULT_TEXT_COLOR_DATE),
                )
                .font(
                    R.styleable.MessageListView_ermisUiMessageTextFontAssetsDate,
                    R.styleable.MessageListView_ermisUiMessageTextFontDate,
                )
                .style(R.styleable.MessageListView_ermisUiMessageTextStyleDate, DEFAULT_TEXT_STYLE)
                .build()

            val textStyleMessageLanguage = TextStyle.Builder(attributes)
                .size(
                    R.styleable.MessageListView_ermisUiMessageTextSizeLanguage,
                    context.getDimension(DEFAULT_TEXT_SIZE_DATE),
                )
                .color(
                    R.styleable.MessageListView_ermisUiMessageTextColorLanguage,
                    context.getColorCompat(DEFAULT_TEXT_COLOR_DATE),
                )
                .font(
                    R.styleable.MessageListView_ermisUiMessageTextFontAssetsLanguage,
                    R.styleable.MessageListView_ermisUiMessageTextFontLanguage,
                )
                .style(R.styleable.MessageListView_ermisUiMessageTextStyleLanguage, DEFAULT_TEXT_STYLE)
                .build()

            val textStyleThreadCounter = TextStyle.Builder(attributes)
                .size(
                    R.styleable.MessageListView_ermisUiMessageTextSizeThreadCounter,
                    context.getDimension(DEFAULT_TEXT_SIZE_THREAD_COUNTER),
                )
                .color(
                    R.styleable.MessageListView_ermisUiMessageTextColorThreadCounter,
                    context.getColorCompat(DEFAULT_TEXT_COLOR_THREAD_COUNTER),
                )
                .font(
                    R.styleable.MessageListView_ermisUiMessageTextFontAssetsThreadCounter,
                    R.styleable.MessageListView_ermisUiMessageTextFontThreadCounter,
                    mediumTypeface,
                )
                .style(R.styleable.MessageListView_ermisUiMessageTextStyleThreadCounter, DEFAULT_TEXT_STYLE)
                .build()

            val textStyleReadCounter = TextStyle.Builder(attributes)
                .size(
                    R.styleable.MessageListView_ermisUiMessageTextSizeReadCounter,
                    context.getDimension(DEFAULT_TEXT_SIZE_READ_COUNTER),
                )
                .color(
                    R.styleable.MessageListView_ermisUiMessageTextColorReadCounter,
                    context.getColorCompat(DEFAULT_TEXT_COLOR_READ_COUNTER),
                )
                .font(
                    R.styleable.MessageListView_ermisUiMessageTextFontAssetsReadCounter,
                    R.styleable.MessageListView_ermisUiMessageTextFontReadCounter,
                    mediumTypeface,
                )
                .style(R.styleable.MessageListView_ermisUiMessageTextStyleReadCounter, DEFAULT_TEXT_STYLE)
                .build()

            val textStyleThreadSeparator = TextStyle.Builder(attributes)
                .size(
                    R.styleable.MessageListView_ermisUiMessageTextSizeThreadSeparator,
                    context.getDimension(DEFAULT_TEXT_SIZE_THREAD_COUNTER),
                )
                .color(
                    R.styleable.MessageListView_ermisUiMessageTextColorThreadSeparator,
                    context.getColorCompat(DEFAULT_TEXT_COLOR_THREAD_COUNTER),
                )
                .font(
                    R.styleable.MessageListView_ermisUiMessageTextFontAssetsThreadSeparator,
                    R.styleable.MessageListView_ermisUiMessageTextFontThreadSeparator,
                    mediumTypeface,
                )
                .style(R.styleable.MessageListView_ermisUiMessageTextStyleThreadSeparator, DEFAULT_TEXT_STYLE)
                .build()

            val textStyleLinkTitle = TextStyle.Builder(attributes)
                .size(
                    R.styleable.MessageListView_ermisUiMessageTextSizeLinkTitle,
                    context.getDimension(DEFAULT_TEXT_SIZE),
                )
                .color(
                    R.styleable.MessageListView_ermisUiMessageTextColorLinkTitle,
                    context.getColorCompat(DEFAULT_TEXT_COLOR),
                )
                .font(
                    R.styleable.MessageListView_ermisUiMessageTextFontAssetsLinkTitle,
                    R.styleable.MessageListView_ermisUiMessageTextFontLinkTitle,
                    boldTypeface,
                )
                .style(R.styleable.MessageListView_ermisUiMessageTextStyleLinkTitle, DEFAULT_TEXT_STYLE)
                .build()

            val textStyleLinkDescription = TextStyle.Builder(attributes)
                .size(
                    R.styleable.MessageListView_ermisUiMessageTextSizeLinkDescription,
                    context.getDimension(DEFAULT_TEXT_SIZE_LINK_DESCRIPTION),
                )
                .color(
                    R.styleable.MessageListView_ermisUiMessageTextColorLinkDescription,
                    context.getColorCompat(DEFAULT_TEXT_COLOR_LINK_DESCRIPTION),
                )
                .font(
                    R.styleable.MessageListView_ermisUiMessageTextFontAssetsLinkDescription,
                    R.styleable.MessageListView_ermisUiMessageTextFontLinkDescription,
                )
                .style(R.styleable.MessageListView_ermisUiMessageTextStyleLinkDescription, DEFAULT_TEXT_STYLE)
                .build()

            val textStyleLinkLabel = TextStyle.Builder(attributes)
                .size(
                    R.styleable.MessageListView_ermisUiMessageTextSizeLinkLabel,
                    context.getDimension(DEFAULT_TEXT_SIZE_LINK_DESCRIPTION),
                )
                .color(
                    R.styleable.MessageListView_ermisUiMessageTextColorLinkLabel,
                    context.getColorCompat(DEFAULT_TEXT_COLOR_LINK_DESCRIPTION),
                )
                .font(
                    R.styleable.MessageListView_ermisUiMessageTextFontAssetsLinkLabel,
                    R.styleable.MessageListView_ermisUiMessageTextFontLinkLabel,
                )
                .style(R.styleable.MessageListView_ermisUiMessageTextStyleLinkLabel, DEFAULT_TEXT_STYLE)
                .build()

            val dateSeparatorBackgroundColor =
                attributes.getColor(
                    R.styleable.MessageListView_ermisUiDateSeparatorBackgroundColor,
                    context.getColorCompat(R.color.ui_overlay_dark),
                )

            val textStyleDateSeparator = TextStyle.Builder(attributes)
                .size(
                    R.styleable.MessageListView_ermisUiMessageTextSizeDateSeparator,
                    context.getDimension(DEFAULT_TEXT_SIZE_DATE_SEPARATOR),
                )
                .color(
                    R.styleable.MessageListView_ermisUiMessageTextColorDateSeparator,
                    context.getColorCompat(DEFAULT_TEXT_COLOR_DATE_SEPARATOR),
                )
                .font(
                    R.styleable.MessageListView_ermisUiMessageTextFontAssetsDateSeparator,
                    R.styleable.MessageListView_ermisUiMessageTextFontDateSeparator,
                )
                .style(R.styleable.MessageListView_ermisUiMessageTextStyleDateSeparator, DEFAULT_TEXT_STYLE)
                .build()

            val reactionsViewStyle = ViewReactionsViewStyle.Companion.Builder(attributes, context)
                .bubbleBorderColorMine(R.styleable.MessageListView_ermisUiMessageReactionsBubbleBorderColorMine)
                .bubbleBorderColorTheirs(R.styleable.MessageListView_ermisUiMessageReactionsBubbleBorderColorTheirs)
                .bubbleBorderWidthMine(R.styleable.MessageListView_ermisUiMessageReactionsBubbleBorderWidthMine)
                .bubbleBorderWidthTheirs(R.styleable.MessageListView_ermisUiMessageReactionsBubbleBorderWidthTheirs)
                .bubbleColorMine(R.styleable.MessageListView_ermisUiMessageReactionsBubbleColorMine)
                .bubbleColorTheirs(R.styleable.MessageListView_ermisUiMessageReactionsBubbleColorTheirs)
                .build()

            val editReactionsViewStyle = EditReactionsViewStyle.Builder(attributes, context)
                .bubbleColorMine(R.styleable.MessageListView_ermisUiEditReactionsBubbleColorMine)
                .bubbleColorTheirs(R.styleable.MessageListView_ermisUiEditReactionsBubbleColorTheirs)
                .reactionsColumns(R.styleable.MessageListView_ermisUiEditReactionsColumns)
                .build()

            val showMessageDeliveryStatusIndicator = attributes.getBoolean(
                R.styleable.MessageListView_ermisUiShowMessageDeliveryStatusIndicator,
                true,
            )

            val iconIndicatorSent = attributes.getDrawable(
                R.styleable.MessageListView_ermisUiIconIndicatorSent,
            ) ?: context.getDrawableCompat(R.drawable.ic_check_single)!!
            val iconIndicatorRead = attributes.getDrawable(
                R.styleable.MessageListView_ermisUiIconIndicatorRead,
            ) ?: context.getDrawableCompat(R.drawable.ic_check_double)!!
            val iconIndicatorPendingSync = attributes.getDrawableCompat(
                context,
                R.styleable.MessageListView_ermisUiIconIndicatorPendingSync,
            ) ?: AppCompatResources.getDrawable(context, R.drawable.ic_clock)!!

            val iconOnlyVisibleToYou = attributes.getDrawable(
                R.styleable.MessageListView_ermisUiIconOnlyVisibleToYou,
            ) ?: context.getDrawableCompat(R.drawable.ic_icon_eye_off)!!

            val messageDeletedBackground =
                attributes.getColor(
                    R.styleable.MessageListView_ermisUiDeletedMessageBackgroundColor,
                    context.getColorCompat(R.color.ui_grey_whisper),
                )

            val textStyleMessageDeleted = TextStyle.Builder(attributes)
                .size(
                    R.styleable.MessageListView_ermisUiMessageTextSizeMessageDeleted,
                    context.getDimension(DEFAULT_TEXT_SIZE),
                )
                .color(
                    R.styleable.MessageListView_ermisUiMessageTextColorMessageDeleted,
                    context.getColorCompat(R.color.ui_text_color_secondary),
                )
                .font(
                    R.styleable.MessageListView_ermisUiMessageTextFontAssetsMessageDeleted,
                    R.styleable.MessageListView_ermisUiMessageTextFontMessageDeleted,
                )
                .style(R.styleable.MessageListView_ermisUiMessageTextStyleMessageDeleted, Typeface.ITALIC)
                .build()

            val messageDeletedBackgroundMine =
                attributes.getColor(
                    R.styleable.MessageListView_ermisUiDeletedMessageBackgroundColorMine,
                    VALUE_NOT_SET,
                )

            val textStyleMessageDeletedMine = TextStyle.Builder(attributes)
                .size(
                    R.styleable.MessageListView_ermisUiMessageTextSizeMessageDeletedMine,
                    textStyleMessageDeleted.size,
                )
                .color(
                    R.styleable.MessageListView_ermisUiMessageTextColorMessageDeletedMine,
                    textStyleMessageDeleted.color,
                )
                .fontAssetsPath(
                    R.styleable.MessageListView_ermisUiMessageTextFontAssetsMessageDeletedMine,
                    textStyleMessageDeleted.fontAssetsPath,
                )
                .fontResource(
                    R.styleable.MessageListView_ermisUiMessageTextFontMessageDeletedMine,
                    textStyleMessageDeleted.fontResource,
                )
                .style(
                    R.styleable.MessageListView_ermisUiMessageTextStyleMessageDeletedMine,
                    textStyleMessageDeleted.style,
                )
                .build()

            val messageDeletedBackgroundTheirs =
                attributes.getColor(
                    R.styleable.MessageListView_ermisUiDeletedMessageBackgroundColorTheirs,
                    VALUE_NOT_SET,
                )

            val textStyleMessageDeletedTheirs = TextStyle.Builder(attributes)
                .size(
                    R.styleable.MessageListView_ermisUiMessageTextSizeMessageDeletedTheirs,
                    textStyleMessageDeleted.size,
                )
                .color(
                    R.styleable.MessageListView_ermisUiMessageTextColorMessageDeletedTheirs,
                    textStyleMessageDeleted.color,
                )
                .fontAssetsPath(
                    R.styleable.MessageListView_ermisUiMessageTextFontAssetsMessageDeletedTheirs,
                    textStyleMessageDeleted.fontAssetsPath,
                )
                .fontResource(
                    R.styleable.MessageListView_ermisUiMessageTextFontMessageDeletedTheirs,
                    textStyleMessageDeleted.fontResource,
                )
                .style(
                    R.styleable.MessageListView_ermisUiMessageTextStyleMessageDeletedTheirs,
                    textStyleMessageDeleted.style,
                )
                .build()

            val messageStrokeColorMine = attributes.getColor(
                R.styleable.MessageListView_ermisUiMessageStrokeColorMine,
                context.getColorCompat(MESSAGE_STROKE_COLOR_MINE),
            )
            val messageStrokeWidthMine =
                attributes.getDimension(
                    R.styleable.MessageListView_ermisUiMessageStrokeWidthMine,
                    MESSAGE_STROKE_WIDTH_MINE,
                )
            val messageStrokeColorTheirs =
                attributes.getColor(
                    R.styleable.MessageListView_ermisUiMessageStrokeColorTheirs,
                    context.getColorCompat(
                        MESSAGE_STROKE_COLOR_THEIRS,
                    ),
                )
            val messageStrokeWidthTheirs =
                attributes.getDimension(
                    R.styleable.MessageListView_ermisUiMessageStrokeWidthTheirs,
                    MESSAGE_STROKE_WIDTH_THEIRS,
                )

            val textStyleSystemMessage = TextStyle.Builder(attributes)
                .size(
                    R.styleable.MessageListView_ermisUiSystemMessageTextSize,
                    context.getDimension(R.dimen.ui_text_small),
                )
                .color(
                    R.styleable.MessageListView_ermisUiSystemMessageTextColor,
                    context.getColorCompat(R.color.ui_text_color_secondary),
                )
                .font(
                    R.styleable.MessageListView_ermisUiSystemMessageTextFontAssets,
                    R.styleable.MessageListView_ermisUiSystemMessageTextFont,
                )
                .style(R.styleable.MessageListView_ermisUiSystemMessageTextStyle, Typeface.BOLD)
                .build()

            val textStyleErrorMessage = TextStyle.Builder(attributes)
                .size(
                    R.styleable.MessageListView_ermisUiErrorMessageTextSize,
                    context.getDimension(R.dimen.ui_text_small),
                )
                .color(
                    R.styleable.MessageListView_ermisUiErrorMessageTextColor,
                    context.getColorCompat(R.color.ui_text_color_secondary),
                )
                .font(
                    R.styleable.MessageListView_ermisUiErrorMessageTextFontAssets,
                    R.styleable.MessageListView_ermisUiErrorMessageTextFont,
                )
                .style(R.styleable.MessageListView_ermisUiErrorMessageTextStyle, Typeface.BOLD)
                .build()

            val pinnedMessageIndicatorTextStyle = TextStyle.Builder(attributes)
                .size(
                    R.styleable.MessageListView_ermisUiPinnedMessageIndicatorTextSize,
                    context.getDimension(R.dimen.ui_text_small),
                )
                .color(
                    R.styleable.MessageListView_ermisUiPinnedMessageIndicatorTextColor,
                    context.getColorCompat(R.color.ui_text_color_secondary),
                )
                .font(
                    R.styleable.MessageListView_ermisUiPinnedMessageIndicatorTextFontAssets,
                    R.styleable.MessageListView_ermisUiPinnedMessageIndicatorTextFont,
                )
                .style(R.styleable.MessageListView_ermisUiPinnedMessageIndicatorTextStyle, Typeface.NORMAL)
                .build()

            val pinnedMessageIndicatorIcon = attributes.getDrawable(
                R.styleable.MessageListView_ermisUiPinnedMessageIndicatorIcon,
            ) ?: context.getDrawableCompat(R.drawable.ic_pin)!!

            val pinnedMessageBackgroundColor = attributes.getColor(
                R.styleable.MessageListView_ermisUiPinnedMessageBackgroundColor,
                context.getColorCompat(R.color.ui_highlight),
            )

            val messageStartMargin = attributes.getDimension(
                R.styleable.MessageListView_ermisUiMessageStartMargin,
                context.getDimension(R.dimen.ermis_ui_message_viewholder_avatar_missing_margin).toFloat(),
            ).toInt()

            val messageEndMargin = attributes.getDimension(
                R.styleable.MessageListView_ermisUiMessageEndMargin,
                context.getDimension(R.dimen.ermis_ui_message_viewholder_avatar_missing_margin).toFloat(),
            ).toInt()

            val messageMaxWidthFactorMine = attributes.getFraction(
                R.styleable.MessageListView_ermisUiMessageMaxWidthFactorMine,
                BASE_MESSAGE_MAX_WIDTH_FACTOR,
                BASE_MESSAGE_MAX_WIDTH_FACTOR,
                DEFAULT_MESSAGE_MAX_WIDTH_FACTOR,
            )

            val messageMaxWidthFactorTheirs = attributes.getFraction(
                R.styleable.MessageListView_ermisUiMessageMaxWidthFactorTheirs,
                BASE_MESSAGE_MAX_WIDTH_FACTOR,
                BASE_MESSAGE_MAX_WIDTH_FACTOR,
                DEFAULT_MESSAGE_MAX_WIDTH_FACTOR,
            )

            val iconFailedMessage = attributes.getDrawable(R.styleable.MessageListView_ermisUiIconFailedIndicator)
                ?: ContextCompat.getDrawable(context, R.drawable.ic_warning)!!
            val iconBannedMessage = attributes.getDrawable(R.styleable.MessageListView_ermisUiIconBannedIndicator)
                ?: ContextCompat.getDrawable(context, R.drawable.ic_warning)!!

            val loadingMoreView = attributes.getResourceId(
                R.styleable.MessageListView_ermisUiMessageListLoadingMoreView,
                R.layout.message_list_loading_more_view,
            )

            val unreadSeparatorBackgroundColor = attributes.getColor(
                R.styleable.MessageListView_ermisUiUnreadSeparatorBackgroundColor,
                context.getColorCompat(R.color.ui_unread_label_background_color),
            )

            val unreadSeparatorTextStyle = TextStyle.Builder(attributes)
                .size(
                    R.styleable.MessageListView_ermisUiUnreadSeparatorTextSize,
                    context.getDimension(R.dimen.ui_text_small),
                )
                .color(
                    R.styleable.MessageListView_ermisUiUnreadSeparatorTextColor,
                    context.getColorCompat(R.color.ui_unread_label_text_color),
                )
                .font(
                    R.styleable.MessageListView_ermisUiUnreadSeparatorTextFontAssets,
                    R.styleable.MessageListView_ermisUiUnreadSeparatorTextFont,
                )
                .style(R.styleable.MessageListView_ermisUiUnreadSeparatorTextStyle, Typeface.BOLD)
                .build()

            return MessageListItemStyle(
                messageBackgroundColorMine = messageBackgroundColorMine.nullIfNotSet(),
                messageBackgroundColorTheirs = messageBackgroundColorTheirs.nullIfNotSet(),
                messageLinkTextColorMine = messageLinkTextColorMine.nullIfNotSet(),
                messageLinkTextColorTheirs = messageLinkTextColorTheirs.nullIfNotSet(),
                messageLinkBackgroundColorMine = linkBackgroundColorMine,
                messageLinkBackgroundColorTheirs = linkBackgroundColorTheirs,
                linkDescriptionMaxLines = linkDescriptionMaxLines,
                textStyleMine = textStyleMine,
                textStyleTheirs = textStyleTheirs,
                textStyleUserName = textStyleUserName,
                textStyleMessageDate = textStyleMessageDate,
                textStyleMessageLanguage = textStyleMessageLanguage,
                textStyleThreadCounter = textStyleThreadCounter,
                textStyleReadCounter = textStyleReadCounter,
                threadSeparatorTextStyle = textStyleThreadSeparator,
                textStyleLinkTitle = textStyleLinkTitle,
                textStyleLinkDescription = textStyleLinkDescription,
                textStyleLinkLabel = textStyleLinkLabel,
                dateSeparatorBackgroundColor = dateSeparatorBackgroundColor,
                textStyleDateSeparator = textStyleDateSeparator,
                reactionsViewStyle = reactionsViewStyle,
                editReactionsViewStyle = editReactionsViewStyle,
                iconIndicatorSent = iconIndicatorSent,
                iconIndicatorRead = iconIndicatorRead,
                iconIndicatorPendingSync = iconIndicatorPendingSync,
                iconOnlyVisibleToYou = iconOnlyVisibleToYou,
                messageDeletedBackground = messageDeletedBackground,
                textStyleMessageDeleted = textStyleMessageDeleted,
                messageDeletedBackgroundMine = messageDeletedBackgroundMine.nullIfNotSet(),
                textStyleMessageDeletedMine = textStyleMessageDeletedMine.nullIfEqualsTo(textStyleMessageDeleted),
                messageDeletedBackgroundTheirs = messageDeletedBackgroundTheirs.nullIfNotSet(),
                textStyleMessageDeletedTheirs = textStyleMessageDeletedTheirs.nullIfEqualsTo(textStyleMessageDeleted),
                messageStrokeColorMine = messageStrokeColorMine,
                messageStrokeWidthMine = messageStrokeWidthMine,
                messageStrokeColorTheirs = messageStrokeColorTheirs,
                messageStrokeWidthTheirs = messageStrokeWidthTheirs,
                textStyleSystemMessage = textStyleSystemMessage,
                textStyleErrorMessage = textStyleErrorMessage,
                pinnedMessageIndicatorTextStyle = pinnedMessageIndicatorTextStyle,
                pinnedMessageIndicatorIcon = pinnedMessageIndicatorIcon,
                pinnedMessageBackgroundColor = pinnedMessageBackgroundColor,
                messageStartMargin = messageStartMargin,
                messageEndMargin = messageEndMargin,
                messageMaxWidthFactorMine = messageMaxWidthFactorMine,
                messageMaxWidthFactorTheirs = messageMaxWidthFactorTheirs,
                showMessageDeliveryStatusIndicator = showMessageDeliveryStatusIndicator,
                iconFailedMessage = iconFailedMessage,
                iconBannedMessage = iconBannedMessage,
                systemMessageAlignment = systemMessageGravity,
                loadingMoreView = loadingMoreView,
                unreadSeparatorBackgroundColor = unreadSeparatorBackgroundColor,
                unreadSeparatorTextStyle = unreadSeparatorTextStyle,
            ).let(TransformStyle.messageListItemStyleTransformer::transform)
                .also { style -> style.checkMessageMaxWidthFactorsRange() }
        }

        private fun Int.nullIfNotSet(): Int? {
            return if (this == VALUE_NOT_SET) null else this
        }

        private fun TextStyle.nullIfEqualsTo(defaultValue: TextStyle): TextStyle? {
            return if (this == defaultValue) null else this
        }

        private fun MessageListItemStyle.checkMessageMaxWidthFactorsRange() {
            require(messageMaxWidthFactorMine in 0.75..1.0) { "messageMaxWidthFactorMine cannot be lower than 0.75 and greater than 1! Current value: $messageMaxWidthFactorMine" }
            require(messageMaxWidthFactorTheirs in 0.75..1.0) { "messageMaxWidthFactorTheirs cannot be lower than 0.75 and greater than 1! Current value: $messageMaxWidthFactorTheirs" }
        }
    }
}
