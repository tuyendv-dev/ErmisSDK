
package network.ermis.ui.view.messages.adapter.view

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.annotation.ColorInt
import network.ermis.ui.components.R
import network.ermis.ui.view.messages.adapter.view.internal.MediaAttachmentView
import network.ermis.ui.font.TextStyle
import network.ermis.ui.helper.TransformStyle
import network.ermis.ui.helper.ViewStyle
import network.ermis.ui.utils.extensions.getColorCompat
import network.ermis.ui.utils.extensions.getColorOrNull
import network.ermis.ui.utils.extensions.getDimension
import network.ermis.ui.utils.extensions.getDimensionOrNull
import network.ermis.ui.utils.extensions.getDrawableCompat
import network.ermis.ui.utils.extensions.use

/**
 * Style for [MediaAttachmentView].
 * Use this class together with [TransformStyle.mediaAttachmentStyleTransformer] to change styles programmatically.
 *
 * @param progressIcon Animated progress drawable. Default value is
 * [R.drawable.rotating_indeterminate_progress_gradient].
 * @param placeholderIcon Displayed while the media preview is Loading.
 * @param placeholderIconTint The tint applied to the placeholder icon displayed before a media
 * attachment preview was loaded or after loading had failed.
 * Default value is [R.drawable.picture_placeholder].
 * @param mediaPreviewBackgroundColor Controls the background color of image and video attachment previews.
 * Default value is [R.color.ui_grey].
 * @param moreCountOverlayColor More count semi-transparent overlay color. Default value is [R.color.ui_overlay].
 * @param moreCountTextStyle Appearance for "more count" text.
 * @param playVideoIcon The icon overlaid above previews of video attachments.
 * @param playVideoIconTint The tint of the play video icon.
 * Default value is [R.drawable.ic_play]
 * @param playVideoIconBackgroundColor Applies a background colour to the View hosting the play video icon.
 * Default value is [R.color.ui_literal_white]
 * @param playVideoIconElevation Determines the elevation of the play video button.
 * @param playVideoIconPaddingTop Determines the padding set between the top of the play video icon and its
 * parent.
 * @param playVideoIconPaddingBottom Determines the padding set between the bottom of the play video icon and its
 * parent.
 * @param playVideoIconPaddingStart Determines the padding set between the start of the play video icon and its
 * parent.
 * @param playVideoIconPaddingEnd Determines the padding set between the end of the play video icon and its
 * parent.
 * @param playVideoIconCornerRadius Determines the corner radius of the play video icon.
 */
public data class MediaAttachmentViewStyle(
    public val progressIcon: Drawable,
    public val placeholderIcon: Drawable,
    @ColorInt public val placeholderIconTint: Int?,
    @ColorInt val mediaPreviewBackgroundColor: Int,
    @ColorInt val moreCountOverlayColor: Int,
    public val moreCountTextStyle: TextStyle,
    public val playVideoIcon: Drawable?,
    @ColorInt val playVideoIconTint: Int?,
    @ColorInt public val playVideoIconBackgroundColor: Int,
    public val playVideoIconElevation: Float,
    public val playVideoIconPaddingTop: Int,
    public val playVideoIconPaddingBottom: Int,
    public val playVideoIconPaddingStart: Int,
    public val playVideoIconPaddingEnd: Int,
    public val playVideoIconCornerRadius: Float,
) : ViewStyle {
    internal companion object {
        /**
         * Fetches styled attributes and returns them wrapped inside of [MediaAttachmentViewStyle].
         */
        @Suppress("LongMethod")
        operator fun invoke(context: Context, attrs: AttributeSet?): MediaAttachmentViewStyle {
            context.obtainStyledAttributes(
                attrs,
                R.styleable.MediaAttachmentView,
                R.attr.ermisUiMessageListMediaAttachmentStyle,
                R.style.ermisUi_MessageList_MediaAttachment,
            ).use { a ->
                val progressIcon = a.getDrawable(R.styleable.MediaAttachmentView_ermisUiMediaAttachmentProgressIcon)
                    ?: context.getDrawableCompat(R.drawable.rotating_indeterminate_progress_gradient)!!

                val mediaPreviewBackgroundColor = a.getColor(
                    R.styleable.MediaAttachmentView_ermisUiMediaAttachmentMediaPreviewBackgroundColor,
                    context.getColorCompat(R.color.ui_message_list_image_attachment_background),
                )

                val moreCountOverlayColor = a.getColor(
                    R.styleable.MediaAttachmentView_ermisUiMediaAttachmentMoreCountOverlayColor,
                    context.getColorCompat(R.color.ui_overlay),
                )

                val moreCountTextStyle = TextStyle.Builder(a)
                    .size(
                        R.styleable.MediaAttachmentView_ermisUiMediaAttachmentMoreCountTextSize,
                        context.getDimension(R.dimen.ermis_ui_message_image_attachment_more_count_text_size),
                    )
                    .color(
                        R.styleable.MediaAttachmentView_ermisUiMediaAttachmentMoreCountTextColor,
                        context.getColorCompat(R.color.ui_literal_white),
                    )
                    .font(
                        R.styleable.MediaAttachmentView_ermisUiMediaAttachmentMoreCountFontAssets,
                        R.styleable.MediaAttachmentView_ermisUiMediaAttachmentMoreCountTextFont,
                    )
                    .style(
                        R.styleable.MediaAttachmentView_ermisUiMediaAttachmentMoreCountTextStyle,
                        Typeface.NORMAL,
                    )
                    .build()

                val placeholderIcon =
                    a.getDrawable(R.styleable.MediaAttachmentView_ermisUiMediaAttachmentPlaceHolderIcon)
                        ?: context.getDrawableCompat(R.drawable.picture_placeholder)!!

                val placeholderIconTint = a.getColorOrNull(
                    R.styleable.MediaAttachmentView_ermisUiMediaAttachmentPlaceHolderIconTint,
                )

                val playVideoIcon = a.getDrawable(R.styleable.MediaAttachmentView_ermisUiMediaAttachmentPlayVideoIcon)
                    ?: context.getDrawableCompat(R.drawable.ic_play)

                val playVideoIconTint = a.getColorOrNull(
                    R.styleable.MediaAttachmentView_ermisUiMediaAttachmentPlayVideoIconTint,
                )

                val playVideoIconBackgroundColor =
                    a.getColor(
                        R.styleable.MediaAttachmentView_ermisUiMediaAttachmentPlayVideoIconBackgroundColor,
                        context.getColorCompat(R.color.ui_literal_white),
                    )

                val playVideoIconCornerRadius =
                    a.getDimension(
                        R.styleable.MediaAttachmentView_ermisUiMediaAttachmentPlayVideoIconCornerRadius,
                        0f,
                    )

                val playVideoIconElevation =
                    a.getDimension(
                        R.styleable.MediaAttachmentView_ermisUiMediaAttachmentPlayVideoIconElevation,
                        0f,
                    )

                val playVideoIconPaddingTop =
                    a.getDimensionPixelSize(
                        R.styleable.MediaAttachmentView_ermisUiMediaAttachmentPlayVideoIconPaddingTop,
                        0,
                    )

                val playVideoIconPaddingBottom =
                    a.getDimensionPixelSize(
                        R.styleable.MediaAttachmentView_ermisUiMediaAttachmentPlayVideoIconPaddingBottom,
                        0,
                    )

                val playVideoIconPaddingStart =
                    a.getDimensionPixelSize(
                        R.styleable.MediaAttachmentView_ermisUiMediaAttachmentPlayVideoIconPaddingStart,
                        0,
                    )

                val playVideoIconPaddingEnd =
                    a.getDimensionPixelSize(
                        R.styleable.MediaAttachmentView_ermisUiMediaAttachmentPlayVideoIconPaddingEnd,
                        0,
                    )

                val playVideoIconPadding =
                    a.getDimensionOrNull(
                        R.styleable.MediaAttachmentView_ermisUiMediaAttachmentPlayVideoIconPadding,
                    )?.toInt()

                return MediaAttachmentViewStyle(
                    progressIcon = progressIcon,
                    mediaPreviewBackgroundColor = mediaPreviewBackgroundColor,
                    moreCountOverlayColor = moreCountOverlayColor,
                    moreCountTextStyle = moreCountTextStyle,
                    placeholderIcon = placeholderIcon,
                    placeholderIconTint = placeholderIconTint,
                    playVideoIcon = playVideoIcon,
                    playVideoIconTint = playVideoIconTint,
                    playVideoIconBackgroundColor = playVideoIconBackgroundColor,
                    playVideoIconElevation = playVideoIconElevation,
                    playVideoIconPaddingTop = playVideoIconPadding ?: playVideoIconPaddingTop,
                    playVideoIconPaddingBottom = playVideoIconPadding ?: playVideoIconPaddingBottom,
                    playVideoIconPaddingStart = playVideoIconPadding ?: playVideoIconPaddingStart,
                    playVideoIconPaddingEnd = playVideoIconPadding ?: playVideoIconPaddingEnd,
                    playVideoIconCornerRadius = playVideoIconCornerRadius,
                ).let(TransformStyle.mediaAttachmentStyleTransformer::transform)
            }
        }
    }
}
