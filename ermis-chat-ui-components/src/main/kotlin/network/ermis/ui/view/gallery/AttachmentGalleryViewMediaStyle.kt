
package network.ermis.ui.view.gallery

import android.app.ActionBar
import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import network.ermis.ui.components.R
import network.ermis.ui.helper.TransformStyle
import network.ermis.ui.helper.ViewStyle
import network.ermis.ui.utils.extensions.getColorCompat
import network.ermis.ui.utils.extensions.getColorOrNull
import network.ermis.ui.utils.extensions.getDimensionOrNull
import network.ermis.ui.utils.extensions.getDrawableCompat

/**
 * Controls the appearance of the main portion of the attachment gallery used to view media.
 *
 * @param viewMediaPlayVideoButtonIcon The drawable for the play button icon displayed above videos in the main viewing
 * area of the gallery.
 * @param viewMediaPlayVideoButtonIcon The drawable for the play button icon displayed above videos in the main viewing
 * area of the gallery.
 * @param viewMediaPlayVideoIconTint Tints the icon overlaid above videos in the main viewing area of the gallery.
 * @param viewMediaPlayVideoIconBackgroundColor The background color of the View containing the play button overlaid
 * above the main viewing area of the gallery.
 * @param viewMediaPlayVideoIconCornerRadius The corner radius of the play button in the main viewing area of the
 * gallery.
 * @param viewMediaPlayVideoIconElevation The elevation of the play button in the main viewing area of the gallery.
 * @param viewMediaPlayVideoIconPaddingTop Sets the top padding of the play video icon displayed above the main viewing
 * area of the gallery.
 * @param viewMediaPlayVideoIconPaddingBottom Sets the bottom padding of the play video icon in the main viewing
 * area of the gallery.
 * @param viewMediaPlayVideoIconPaddingStart Sets the start padding of the play video icon in the main viewing
 * area of the gallery.
 * @param viewMediaPlayVideoIconPaddingEnd  Sets the end padding of the play video icon in the main viewing
 * area of the gallery.
 * @param viewMediaPlayVideoIconWidth Sets the width of the play video button in the main viewing area of the gallery.
 * @param viewMediaPlayVideoIconHeight Sets the width of the play video button in the main viewing area of the gallery.
 * @param imagePlaceholder A placeholder drawable used before the image is loaded.
 */
public data class AttachmentGalleryViewMediaStyle(
    val viewMediaPlayVideoButtonIcon: Drawable?,
    @ColorInt val viewMediaPlayVideoIconTint: Int?,
    @ColorInt val viewMediaPlayVideoIconBackgroundColor: Int,
    val viewMediaPlayVideoIconCornerRadius: Float,
    val viewMediaPlayVideoIconElevation: Float,
    val viewMediaPlayVideoIconPaddingTop: Int,
    val viewMediaPlayVideoIconPaddingBottom: Int,
    val viewMediaPlayVideoIconPaddingStart: Int,
    val viewMediaPlayVideoIconPaddingEnd: Int,
    val viewMediaPlayVideoIconWidth: Int,
    val viewMediaPlayVideoIconHeight: Int,
    val imagePlaceholder: Drawable?,
) : ViewStyle {

    internal companion object {

        operator fun invoke(context: Context, attrs: AttributeSet?): AttachmentGalleryViewMediaStyle {
            context.obtainStyledAttributes(
                attrs,
                R.styleable.AttachmentGalleryVideoAttachments,
                R.attr.ermisUiAttachmentGalleryVideoAttachmentsStyle,
                R.style.ermisUi_AttachmentGallery_VideoAttachments,
            ).let { styledAttributes ->
                val style = AttachmentGalleryViewMediaStyle(context, styledAttributes)
                styledAttributes.recycle()
                return style
            }
        }

        operator fun invoke(context: Context, it: TypedArray): AttachmentGalleryViewMediaStyle {
            val viewMediaPlayVideoButtonIcon = it.getDrawable(
                R.styleable.AttachmentGalleryVideoAttachments_ermisUiAttachmentGalleryViewMediaPlayVideoButtonIcon,
            ) ?: context.getDrawableCompat(R.drawable.ic_play)

            val viewMediaPlayVideoIconTint = it.getColorOrNull(
                R.styleable.AttachmentGalleryVideoAttachments_ermisUiAttachmentGalleryViewMediaPlayVideoIconTint,
            )

            val viewMediaPlayVideoIconBackgroundColor =
                it.getColor(
                    R.styleable.AttachmentGalleryVideoAttachments_ermisUiAttachmentGalleryViewMediaPlayVideoIconBackgroundColor,
                    context.getColorCompat(R.color.ui_literal_white),
                )

            val viewMediaPlayVideoIconCornerRadius =
                it.getDimension(
                    R.styleable.AttachmentGalleryVideoAttachments_ermisUiAttachmentGalleryViewMediaPlayVideoIconCornerRadius,
                    0f,
                )

            val viewMediaPlayVideoIconElevation =
                it.getDimension(
                    R.styleable.AttachmentGalleryVideoAttachments_ermisUiAttachmentGalleryViewMediaPlayVideoIconElevation,
                    0f,
                )

            val viewMediaPlayVideoIconPaddingTop =
                it.getDimensionPixelSize(
                    R.styleable.AttachmentGalleryVideoAttachments_ermisUiAttachmentGalleryViewMediaPlayVideoIconPaddingTop,
                    0,
                )

            val viewMediaPlayVideoIconPaddingBottom =
                it.getDimensionPixelSize(
                    R.styleable.AttachmentGalleryVideoAttachments_ermisUiAttachmentGalleryViewMediaPlayVideoIconPaddingBottom,
                    0,
                )

            val viewMediaPlayVideoIconPaddingStart =
                it.getDimensionPixelSize(
                    R.styleable.AttachmentGalleryVideoAttachments_ermisUiAttachmentGalleryViewMediaPlayVideoIconPaddingStart,
                    0,
                )

            val viewMediaPlayVideoIconPaddingEnd =
                it.getDimensionPixelSize(
                    R.styleable.AttachmentGalleryVideoAttachments_ermisUiAttachmentGalleryViewMediaPlayVideoIconPaddingEnd,
                    0,
                )

            val viewMediaPlayVideoIconPadding =
                it.getDimensionOrNull(
                    R.styleable.AttachmentGalleryVideoAttachments_ermisUiAttachmentGalleryViewMediaPlayVideoIconPadding,
                )?.toInt()

            val viewMediaPlayVideoIconWidth =
                it.getLayoutDimension(
                    R.styleable.AttachmentGalleryVideoAttachments_ermisUiAttachmentGalleryViewMediaPlayIconWidth,
                    ActionBar.LayoutParams.WRAP_CONTENT,
                )

            val viewMediaPlayVideoIconHeight =
                it.getLayoutDimension(
                    R.styleable.AttachmentGalleryVideoAttachments_ermisUiAttachmentGalleryViewMediaPlayIconHeight,
                    ActionBar.LayoutParams.WRAP_CONTENT,
                )

            val imagePlaceholder = it.getDrawable(
                R.styleable.AttachmentGalleryVideoAttachments_ermisUiAttachmentGalleryViewMediaImagePlaceholder,
            ) ?: ContextCompat.getDrawable(
                context,
                R.drawable.picture_placeholder,
            )

            return AttachmentGalleryViewMediaStyle(
                viewMediaPlayVideoButtonIcon = viewMediaPlayVideoButtonIcon,
                viewMediaPlayVideoIconTint = viewMediaPlayVideoIconTint,
                viewMediaPlayVideoIconBackgroundColor = viewMediaPlayVideoIconBackgroundColor,
                viewMediaPlayVideoIconCornerRadius = viewMediaPlayVideoIconCornerRadius,
                viewMediaPlayVideoIconElevation = viewMediaPlayVideoIconElevation,
                viewMediaPlayVideoIconPaddingTop = viewMediaPlayVideoIconPadding ?: viewMediaPlayVideoIconPaddingTop,
                viewMediaPlayVideoIconPaddingBottom = viewMediaPlayVideoIconPadding
                    ?: viewMediaPlayVideoIconPaddingBottom,
                viewMediaPlayVideoIconPaddingStart = viewMediaPlayVideoIconPadding
                    ?: viewMediaPlayVideoIconPaddingStart,
                viewMediaPlayVideoIconPaddingEnd = viewMediaPlayVideoIconPadding ?: viewMediaPlayVideoIconPaddingEnd,
                viewMediaPlayVideoIconWidth = viewMediaPlayVideoIconWidth,
                viewMediaPlayVideoIconHeight = viewMediaPlayVideoIconHeight,
                imagePlaceholder = imagePlaceholder,
            ).let(TransformStyle.attachmentGalleryViewMediaStyle::transform)
        }
    }
}
