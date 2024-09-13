
package network.ermis.ui.view.mentions

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

public data class MentionListViewStyle(
    @ColorInt public val backgroundColor: Int,
    public val emptyStateDrawable: Drawable,
    public val messagePreviewStyle: MessagePreviewStyle,
) : ViewStyle {

    internal companion object {
        operator fun invoke(context: Context, attrs: AttributeSet?): MentionListViewStyle {
            context.obtainStyledAttributes(
                attrs,
                R.styleable.MentionListView,
                R.attr.ermisUiMentionListStyle,
                R.style.ermisUi_MentionList,
            ).use { typedArray ->
                val backgroundColor = typedArray.getColor(
                    R.styleable.MentionListView_ermisUiBackground,
                    context.getColorCompat(R.color.ui_white_snow),
                )

                val emptyStateDrawable = typedArray.getDrawable(
                    R.styleable.MentionListView_ermisUiEmptyStateDrawable,
                ) ?: context.getDrawableCompat(R.drawable.ic_mentions_empty)!!

                val senderTextStyle = TextStyle.Builder(typedArray)
                    .size(
                        R.styleable.MentionListView_ermisUiSenderNameTextSize,
                        context.getDimension(R.dimen.ui_text_medium),
                    )
                    .color(
                        R.styleable.MentionListView_ermisUiSenderNameTextColor,
                        context.getColorCompat(R.color.ui_text_color_primary),
                    )
                    .font(
                        R.styleable.MentionListView_ermisUiSenderNameTextFontAssets,
                        R.styleable.MentionListView_ermisUiSenderNameTextFont,
                    )
                    .style(
                        R.styleable.MentionListView_ermisUiSenderNameTextStyle,
                        Typeface.NORMAL,
                    )
                    .build()

                val messageTextStyle = TextStyle.Builder(typedArray)
                    .size(
                        R.styleable.MentionListView_ermisUiMessageTextSize,
                        context.getDimension(R.dimen.ui_text_medium),
                    )
                    .color(
                        R.styleable.MentionListView_ermisUiMessageTextColor,
                        context.getColorCompat(R.color.ui_text_color_primary),
                    )
                    .font(
                        R.styleable.MentionListView_ermisUiMessageTextFontAssets,
                        R.styleable.MentionListView_ermisUiMessageTextFont,
                    )
                    .style(
                        R.styleable.MentionListView_ermisUiMessageTextStyle,
                        Typeface.NORMAL,
                    )
                    .build()

                val messageTimeTextStyle = TextStyle.Builder(typedArray)
                    .size(
                        R.styleable.MentionListView_ermisUiMessageTimeTextSize,
                        context.getDimension(R.dimen.ui_text_medium),
                    )
                    .color(
                        R.styleable.MentionListView_ermisUiMessageTimeTextColor,
                        context.getColorCompat(R.color.ui_text_color_primary),
                    )
                    .font(
                        R.styleable.MentionListView_ermisUiMessageTimeTextFontAssets,
                        R.styleable.MentionListView_ermisUiMessageTimeTextFont,
                    )
                    .style(
                        R.styleable.MentionListView_ermisUiMessageTimeTextStyle,
                        Typeface.NORMAL,
                    )
                    .build()

                return MentionListViewStyle(
                    backgroundColor = backgroundColor,
                    emptyStateDrawable = emptyStateDrawable,
                    messagePreviewStyle = MessagePreviewStyle(
                        messageSenderTextStyle = senderTextStyle,
                        messageTextStyle = messageTextStyle,
                        messageTimeTextStyle = messageTimeTextStyle,
                    ),
                ).let(TransformStyle.mentionListViewStyleTransformer::transform)
            }
        }
    }
}
