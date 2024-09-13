
package network.ermis.ui.view.pinned

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.annotation.ColorInt
import network.ermis.ui.components.R
import network.ermis.ui.view.messages.preview.MessagePreviewStyle
import network.ermis.ui.font.TextStyle
import network.ermis.ui.helper.TransformStyle
import network.ermis.ui.helper.ViewStyle
import network.ermis.ui.utils.extensions.getColorCompat
import network.ermis.ui.utils.extensions.getDimension
import network.ermis.ui.utils.extensions.getDrawableCompat
import network.ermis.ui.utils.extensions.use

public data class PinnedMessageListViewStyle(
    @ColorInt public val backgroundColor: Int,
    public val emptyStateDrawable: Drawable,
    public val messagePreviewStyle: MessagePreviewStyle,
) : ViewStyle {

    internal companion object {
        operator fun invoke(context: Context, attrs: AttributeSet?): PinnedMessageListViewStyle {
            context.obtainStyledAttributes(
                attrs,
                R.styleable.PinnedMessageListView,
                R.attr.ermisUiPinnedMessageListStyle,
                R.style.ermisUi_PinnedMessageList,
            ).use { typedArray ->
                val backgroundColor = typedArray.getColor(
                    R.styleable.PinnedMessageListView_ermisUiPinnedMessageListBackground,
                    context.getColorCompat(R.color.ui_white_snow),
                )

                val emptyStateDrawable = typedArray.getDrawable(
                    R.styleable.PinnedMessageListView_ermisUiPinnedMessageListEmptyStateDrawable,
                ) ?: context.getDrawableCompat(R.drawable.ic_pinned_messages_empty)!!

                val senderTextStyle = TextStyle.Builder(typedArray)
                    .size(
                        R.styleable.PinnedMessageListView_ermisUiPinnedMessageListSenderNameTextSize,
                        context.getDimension(R.dimen.ui_text_medium),
                    )
                    .color(
                        R.styleable.PinnedMessageListView_ermisUiPinnedMessageListSenderNameTextColor,
                        context.getColorCompat(R.color.ui_text_color_primary),
                    )
                    .font(
                        R.styleable.PinnedMessageListView_ermisUiPinnedMessageListSenderNameTextFontAssets,
                        R.styleable.PinnedMessageListView_ermisUiPinnedMessageListSenderNameTextFont,
                    )
                    .style(
                        R.styleable.PinnedMessageListView_ermisUiPinnedMessageListSenderNameTextStyle,
                        Typeface.NORMAL,
                    )
                    .build()

                val messageTextStyle = TextStyle.Builder(typedArray)
                    .size(
                        R.styleable.PinnedMessageListView_ermisUiPinnedMessageListMessageTextSize,
                        context.getDimension(R.dimen.ui_text_medium),
                    )
                    .color(
                        R.styleable.PinnedMessageListView_ermisUiPinnedMessageListMessageTextColor,
                        context.getColorCompat(R.color.ui_text_color_primary),
                    )
                    .font(
                        R.styleable.PinnedMessageListView_ermisUiPinnedMessageListMessageTextFontAssets,
                        R.styleable.PinnedMessageListView_ermisUiPinnedMessageListMessageTextFont,
                    )
                    .style(
                        R.styleable.PinnedMessageListView_ermisUiPinnedMessageListMessageTextStyle,
                        Typeface.NORMAL,
                    )
                    .build()

                val messageTimeTextStyle = TextStyle.Builder(typedArray)
                    .size(
                        R.styleable.PinnedMessageListView_ermisUiPinnedMessageListMessageTimeTextSize,
                        context.getDimension(R.dimen.ui_text_medium),
                    )
                    .color(
                        R.styleable.PinnedMessageListView_ermisUiPinnedMessageListMessageTimeTextColor,
                        context.getColorCompat(R.color.ui_text_color_primary),
                    )
                    .font(
                        R.styleable.PinnedMessageListView_ermisUiPinnedMessageListMessageTimeTextFontAssets,
                        R.styleable.PinnedMessageListView_ermisUiPinnedMessageListMessageTimeTextFont,
                    )
                    .style(
                        R.styleable.PinnedMessageListView_ermisUiPinnedMessageListMessageTimeTextStyle,
                        Typeface.NORMAL,
                    )
                    .build()

                return PinnedMessageListViewStyle(
                    backgroundColor = backgroundColor,
                    emptyStateDrawable = emptyStateDrawable,
                    messagePreviewStyle = MessagePreviewStyle(
                        messageSenderTextStyle = senderTextStyle,
                        messageTextStyle = messageTextStyle,
                        messageTimeTextStyle = messageTimeTextStyle,
                    ),
                ).let(TransformStyle.pinnedMessageListViewStyleTransformer::transform)
            }
        }
    }
}
