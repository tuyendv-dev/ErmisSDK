
package network.ermis.ui.view.messages

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.annotation.Px
import network.ermis.ui.components.R
import network.ermis.ui.font.TextStyle
import network.ermis.ui.helper.TransformStyle
import network.ermis.ui.helper.ViewStyle
import network.ermis.ui.utils.extensions.dpToPx
import network.ermis.ui.utils.extensions.getColorCompat
import network.ermis.ui.utils.extensions.getDimension
import network.ermis.ui.utils.extensions.use

/**
 * Style for unsupported attachments.
 *
 * @param backgroundColor Unsupported attachment background color.
 * @param strokeColor Unsupported attachment stroke color.
 * @param strokeWidth Unsupported attachment stroke width.
 * @param cornerRadius Unsupported attachment corner radius.
 * @param titleTextStyle Text appearance for unsupported attachment title.
 */
public data class UnsupportedAttachmentViewStyle(
    @ColorInt val backgroundColor: Int,
    @ColorInt val strokeColor: Int,
    @Px val strokeWidth: Int,
    @Px val cornerRadius: Int,
    val titleTextStyle: TextStyle,
) : ViewStyle {
    internal companion object {
        operator fun invoke(context: Context, attrs: AttributeSet?): UnsupportedAttachmentViewStyle {
            context.obtainStyledAttributes(
                attrs,
                R.styleable.UnsupportedAttachmentView,
                R.attr.ermisUiMessageListUnsupportedAttachmentStyle,
                R.style.ermisUi_MessageList_UnsupportedAttachment,
            ).use { a ->
                val backgroundColor = a.getColor(
                    R.styleable.UnsupportedAttachmentView_ermisUiUnsupportedAttachmentBackgroundColor,
                    context.getColorCompat(R.color.ui_white),
                )

                val strokeColor = a.getColor(
                    R.styleable.UnsupportedAttachmentView_ermisUiUnsupportedAttachmentStrokeColor,
                    context.getColorCompat(R.color.ui_grey_whisper),
                )

                val strokeWidth = a.getDimensionPixelSize(
                    R.styleable.UnsupportedAttachmentView_ermisUiUnsupportedAttachmentStrokeWidth,
                    1.dpToPx(),
                )

                val cornerRadius = a.getDimensionPixelSize(
                    R.styleable.UnsupportedAttachmentView_ermisUiUnsupportedAttachmentCornerRadius,
                    12.dpToPx(),
                )

                val titleTextStyle = TextStyle.Builder(a)
                    .size(
                        R.styleable.UnsupportedAttachmentView_ermisUiUnsupportedAttachmentTitleTextSize,
                        context.getDimension(R.dimen.ui_text_medium),
                    )
                    .color(
                        R.styleable.UnsupportedAttachmentView_ermisUiUnsupportedAttachmentTitleTextColor,
                        context.getColorCompat(R.color.ui_text_color_primary),
                    )
                    .font(
                        R.styleable.UnsupportedAttachmentView_ermisUiUnsupportedAttachmentTitleFontAssets,
                        R.styleable.UnsupportedAttachmentView_ermisUiUnsupportedAttachmentTitleTextFont,
                    )
                    .style(
                        R.styleable.UnsupportedAttachmentView_ermisUiUnsupportedAttachmentTitleTextStyle,
                        Typeface.NORMAL,
                    )
                    .build()

                return UnsupportedAttachmentViewStyle(
                    backgroundColor = backgroundColor,
                    titleTextStyle = titleTextStyle,
                    strokeColor = strokeColor,
                    strokeWidth = strokeWidth,
                    cornerRadius = cornerRadius,
                ).let(TransformStyle.unsupportedAttachmentStyleTransformer::transform)
            }
        }
    }
}
