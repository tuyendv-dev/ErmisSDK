
package network.ermis.ui.view.messages

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Typeface
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.core.content.res.ResourcesCompat
import network.ermis.ui.components.R
import network.ermis.ui.view.messages.MessageReplyStyle.Companion.MESSAGE_STROKE_COLOR_MINE
import network.ermis.ui.view.messages.MessageReplyStyle.Companion.MESSAGE_STROKE_COLOR_THEIRS
import network.ermis.ui.view.messages.MessageReplyStyle.Companion.MESSAGE_STROKE_WIDTH_MINE
import network.ermis.ui.view.messages.MessageReplyStyle.Companion.MESSAGE_STROKE_WIDTH_THEIRS
import network.ermis.ui.font.TextStyle
import network.ermis.ui.helper.TransformStyle
import network.ermis.ui.helper.ViewStyle
import network.ermis.ui.utils.extensions.dpToPxPrecise
import network.ermis.ui.utils.extensions.getColorCompat
import network.ermis.ui.utils.extensions.getDimension

/**
 * Style for view holders used inside [MessageListView] allowing to customize message "reply" view.
 * Use this class together with [TransformStyle.messageReplyStyleTransformer] to change styles programmatically.
 *
 * @property messageBackgroundColorMine Background color for message sent by the current user. Default value is [R.color.ui_grey_gainsboro].
 * @property messageBackgroundColorTheirs Background color for message sent by other user. Default value is [R.color.ui_white].
 * @property linkBackgroundColorMine Background color of links in the message sent by the current user.
 * @property linkBackgroundColorTheirs Background color of links in the message sent by the other user.
 * @property linkStyleMine Appearance for message link sent by the current user.
 * @property linkStyleTheirs Appearance for message link sent by other users.
 * @property textStyleMine Appearance for message text sent by the current user.
 * @property textStyleTheirs Appearance for message text sent by other users.
 * @property messageStrokeColorMine Stroke color for message sent by the current user. Default value is [MESSAGE_STROKE_COLOR_MINE].
 * @property messageStrokeWidthMine Stroke width for message sent by the current user. Default value is [MESSAGE_STROKE_WIDTH_MINE].
 * @property messageStrokeColorTheirs Stroke color for message sent by other user. Default value is [MESSAGE_STROKE_COLOR_THEIRS].
 * @property messageStrokeWidthTheirs Stroke width for message sent by other user. Default value is [MESSAGE_STROKE_WIDTH_THEIRS].
 */
public data class MessageReplyStyle(
    @ColorInt public val messageBackgroundColorMine: Int,
    @ColorInt public val messageBackgroundColorTheirs: Int,
    @ColorInt public val linkBackgroundColorMine: Int,
    @ColorInt public val linkBackgroundColorTheirs: Int,
    public val textStyleMine: TextStyle,
    public val textStyleTheirs: TextStyle,
    public val linkStyleMine: TextStyle,
    public val linkStyleTheirs: TextStyle,
    @ColorInt public val messageStrokeColorMine: Int,
    @Px public val messageStrokeWidthMine: Float,
    @ColorInt public val messageStrokeColorTheirs: Int,
    @Px public val messageStrokeWidthTheirs: Float,
) : ViewStyle {
    internal companion object {
        operator fun invoke(attributes: TypedArray, context: Context): MessageReplyStyle {
            val messageBackgroundColorMine: Int = attributes.getColor(
                R.styleable.MessageListView_ermisUiMessageReplyBackgroundColorMine,
                VALUE_NOT_SET,
            )
            val messageBackgroundColorTheirs: Int = attributes.getColor(
                R.styleable.MessageListView_ermisUiMessageReplyBackgroundColorTheirs,
                VALUE_NOT_SET,
            )
            val linkBackgroundColorMine = attributes.getColor(
                R.styleable.MessageListView_ermisUiMessageReplyLinkBackgroundColorMine,
                VALUE_NOT_SET,
            )
            val linkBackgroundColorTheirs = attributes.getColor(
                R.styleable.MessageListView_ermisUiMessageReplyLinkBackgroundColorTheirs,
                VALUE_NOT_SET,
            )
            val mediumTypeface = ResourcesCompat.getFont(context, R.font.roboto_medium) ?: Typeface.DEFAULT
            val textStyleMine = TextStyle.Builder(attributes)
                .size(
                    R.styleable.MessageListView_ermisUiMessageReplyTextSizeMine,
                    context.getDimension(DEFAULT_TEXT_SIZE),
                )
                .color(
                    R.styleable.MessageListView_ermisUiMessageReplyTextColorMine,
                    context.getColorCompat(DEFAULT_TEXT_COLOR),
                )
                .font(
                    R.styleable.MessageListView_ermisUiMessageReplyTextFontAssetsMine,
                    R.styleable.MessageListView_ermisUiMessageReplyTextFontMine,
                    mediumTypeface,
                )
                .style(R.styleable.MessageListView_ermisUiMessageReplyTextStyleMine, DEFAULT_TEXT_STYLE)
                .build()

            val textStyleTheirs = TextStyle.Builder(attributes)
                .size(
                    R.styleable.MessageListView_ermisUiMessageReplyTextSizeTheirs,
                    context.getDimension(DEFAULT_TEXT_SIZE),
                )
                .color(
                    R.styleable.MessageListView_ermisUiMessageReplyTextColorTheirs,
                    context.getColorCompat(DEFAULT_TEXT_COLOR),
                )
                .font(
                    R.styleable.MessageListView_ermisUiMessageReplyTextFontAssetsTheirs,
                    R.styleable.MessageListView_ermisUiMessageReplyTextFontTheirs,
                    mediumTypeface,
                )
                .style(
                    R.styleable.MessageListView_ermisUiMessageReplyTextStyleTheirs,
                    DEFAULT_TEXT_STYLE,
                )
                .build()

            val textStyleLinkTheirs = TextStyle.Builder(attributes)
                .color(
                    R.styleable.MessageListView_ermisUiMessageReplyLinkColorTheirs,
                    VALUE_NOT_SET,
                )
                .build()

            val textStyleLinkMine = TextStyle.Builder(attributes)
                .color(
                    R.styleable.MessageListView_ermisUiMessageReplyLinkColorMine,
                    VALUE_NOT_SET,
                )
                .build()

            val messageStrokeColorMine = attributes.getColor(
                R.styleable.MessageListView_ermisUiMessageReplyStrokeColorMine,
                context.getColorCompat(MESSAGE_STROKE_COLOR_MINE),
            )
            val messageStrokeWidthMine =
                attributes.getDimension(
                    R.styleable.MessageListView_ermisUiMessageReplyStrokeWidthMine,
                    MESSAGE_STROKE_WIDTH_MINE,
                )
            val messageStrokeColorTheirs =
                attributes.getColor(
                    R.styleable.MessageListView_ermisUiMessageReplyStrokeColorTheirs,
                    context.getColorCompat(MESSAGE_STROKE_COLOR_THEIRS),
                )
            val messageStrokeWidthTheirs =
                attributes.getDimension(
                    R.styleable.MessageListView_ermisUiMessageReplyStrokeWidthTheirs,
                    MESSAGE_STROKE_WIDTH_THEIRS,
                )

            return MessageReplyStyle(
                messageBackgroundColorMine = messageBackgroundColorMine,
                messageBackgroundColorTheirs = messageBackgroundColorTheirs,
                linkStyleMine = textStyleLinkMine,
                linkStyleTheirs = textStyleLinkTheirs,
                linkBackgroundColorMine = linkBackgroundColorMine,
                linkBackgroundColorTheirs = linkBackgroundColorTheirs,
                textStyleMine = textStyleMine,
                textStyleTheirs = textStyleTheirs,
                messageStrokeColorMine = messageStrokeColorMine,
                messageStrokeColorTheirs = messageStrokeColorTheirs,
                messageStrokeWidthMine = messageStrokeWidthMine,
                messageStrokeWidthTheirs = messageStrokeWidthTheirs,
            ).let(TransformStyle.messageReplyStyleTransformer::transform)
        }

        private val MESSAGE_STROKE_WIDTH_THEIRS: Float = 1.dpToPxPrecise()
        private const val VALUE_NOT_SET = Integer.MAX_VALUE
        internal val DEFAULT_TEXT_COLOR = R.color.ui_text_color_primary
        internal val DEFAULT_TEXT_SIZE = R.dimen.ui_text_medium
        internal const val DEFAULT_TEXT_STYLE = Typeface.NORMAL
        internal val MESSAGE_STROKE_COLOR_MINE = R.color.ui_literal_transparent
        internal const val MESSAGE_STROKE_WIDTH_MINE: Float = 0f
        internal val MESSAGE_STROKE_COLOR_THEIRS = R.color.ui_grey_whisper
    }
}
