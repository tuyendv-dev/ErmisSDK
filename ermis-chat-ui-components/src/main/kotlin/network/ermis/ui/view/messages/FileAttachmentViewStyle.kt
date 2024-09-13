
package network.ermis.ui.view.messages

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
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
import network.ermis.ui.utils.extensions.getDrawableCompat
import network.ermis.ui.utils.extensions.use

public data class FileAttachmentViewStyle(
    @ColorInt val backgroundColor: Int,
    @ColorInt val strokeColor: Int,
    @Px val strokeWidth: Int,
    @Px val cornerRadius: Int,
    val progressBarDrawable: Drawable,
    public val actionButtonIcon: Drawable,
    public val failedAttachmentIcon: Drawable,
    val titleTextStyle: TextStyle,
    val fileSizeTextStyle: TextStyle,
) : ViewStyle {
    internal companion object {
        operator fun invoke(context: Context, attrs: AttributeSet?): FileAttachmentViewStyle {
            context.obtainStyledAttributes(
                attrs,
                R.styleable.FileAttachmentView,
                R.attr.ermisUiMessageListFileAttachmentStyle,
                R.style.ermisUi_MessageList_FileAttachment,
            ).use { a ->
                val progressBarDrawable =
                    a.getDrawable(R.styleable.FileAttachmentView_ermisUiFileAttachmentProgressBarDrawable)
                        ?: context.getDrawableCompat(R.drawable.rotating_indeterminate_progress_gradient)!!

                val backgroundColor = a.getColor(
                    R.styleable.FileAttachmentView_ermisUiFileAttachmentBackgroundColor,
                    context.getColorCompat(R.color.ui_white),
                )

                val actionIcon = a.getDrawable(R.styleable.FileAttachmentView_ermisUiFileAttachmentActionButton)
                    ?: context.getDrawableCompat(R.drawable.ic_icon_download)!!

                val titleTextStyle = TextStyle.Builder(a)
                    .size(
                        R.styleable.FileAttachmentView_ermisUiFileAttachmentTitleTextSize,
                        context.getDimension(R.dimen.ui_text_medium),
                    )
                    .color(
                        R.styleable.FileAttachmentView_ermisUiFileAttachmentTitleTextColor,
                        context.getColorCompat(R.color.ui_text_color_primary),
                    )
                    .font(
                        R.styleable.FileAttachmentView_ermisUiFileAttachmentTitleFontAssets,
                        R.styleable.FileAttachmentView_ermisUiFileAttachmentTitleTextFont,
                    )
                    .style(
                        R.styleable.FileAttachmentView_ermisUiFileAttachmentTitleTextStyle,
                        Typeface.NORMAL,
                    )
                    .build()

                val fileSizeTextStyle = TextStyle.Builder(a)
                    .size(
                        R.styleable.FileAttachmentView_ermisUiFileAttachmentFileSizeTextSize,
                        context.getDimension(R.dimen.ui_text_small),
                    )
                    .color(
                        R.styleable.FileAttachmentView_ermisUiFileAttachmentFileSizeTextColor,
                        context.getColorCompat(R.color.ui_text_color_primary),
                    )
                    .font(
                        R.styleable.FileAttachmentView_ermisUiFileAttachmentFileSizeFontAssets,
                        R.styleable.FileAttachmentView_ermisUiFileAttachmentFileSizeTextFont,
                    )
                    .style(
                        R.styleable.FileAttachmentView_ermisUiFileAttachmentFileSizeTextStyle,
                        Typeface.NORMAL,
                    )
                    .build()

                val failedAttachmentIcon =
                    a.getDrawable(R.styleable.FileAttachmentView_ermisUiFileAttachmentFailedAttachmentIcon)
                        ?: context.getDrawableCompat(R.drawable.ic_warning)!!

                val strokeColor = a.getColor(
                    R.styleable.FileAttachmentView_ermisUiFileAttachmentStrokeColor,
                    context.getColorCompat(R.color.ui_grey_whisper),
                )

                val strokeWidth = a.getDimensionPixelSize(
                    R.styleable.FileAttachmentView_ermisUiFileAttachmentStrokeWidth,
                    1.dpToPx(),
                )

                val cornerRadius = a.getDimensionPixelSize(
                    R.styleable.FileAttachmentView_ermisUiFileAttachmentCornerRadius,
                    12.dpToPx(),
                )

                return FileAttachmentViewStyle(
                    backgroundColor = backgroundColor,
                    progressBarDrawable = progressBarDrawable,
                    actionButtonIcon = actionIcon,
                    titleTextStyle = titleTextStyle,
                    fileSizeTextStyle = fileSizeTextStyle,
                    failedAttachmentIcon = failedAttachmentIcon,
                    strokeColor = strokeColor,
                    strokeWidth = strokeWidth,
                    cornerRadius = cornerRadius,
                ).let(TransformStyle.fileAttachmentStyleTransformer::transform)
            }
        }
    }
}
