
package network.ermis.ui.view.gallery

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import network.ermis.ui.components.R
import network.ermis.ui.view.gallery.overview.MediaAttachmentGridView
import network.ermis.ui.helper.TransformStyle
import network.ermis.ui.helper.ViewStyle
import network.ermis.ui.utils.extensions.getColorCompat
import network.ermis.ui.utils.extensions.getColorOrNull
import network.ermis.ui.utils.extensions.getDimensionOrNull
import network.ermis.ui.utils.extensions.getDrawableCompat

/**
 * Controls the appearance of [MediaAttachmentGridView].
 *
 * @param showUserAvatars Controls whether the avatar of the user who had sent the attachment is displayed
 * over the attachment preview or not.
 * @param playVideoButtonIcon The drawable for the play button icon overlaid above video attachments in
 * the media overview segment of the gallery.
 * @param playVideoIconTint Tints the icon overlaid on top of video attachment previews in the media
 * overview segment of the gallery.
 * @param playVideoIconBackgroundColor The background color of the View containing the play button in
 * the media overview segment of the gallery.
 * @param playVideoIconCornerRadius The corner radius of the play button in the media
 * overview segment of the gallery..
 * @param playVideoIconElevation The elevation of the play button in the media
 * overview segment of the gallery
 * @param playVideoIconPaddingTop Sets the top padding of the play video icon in the media
 * overview segment of the gallery.
 * @param playVideoIconPaddingBottom Sets the bottom padding of the play video icon in the media
 * overview segment of the gallery.
 * @param playVideoIconPaddingStart Sets the start padding of the play video icon in the media
 * overview segment of the gallery.
 * @param playVideoIconPaddingEnd  Sets the end padding of the play video icon in the media
 * overview segment of the gallery.
 * @param imagePlaceholder A placeholder drawable used before the image is loaded.
 */
public data class MediaAttachmentGridViewStyle(
    val showUserAvatars: Boolean,
    val playVideoButtonIcon: Drawable?,
    @ColorInt val playVideoIconTint: Int?,
    @ColorInt val playVideoIconBackgroundColor: Int,
    val playVideoIconCornerRadius: Float,
    val playVideoIconElevation: Float,
    val playVideoIconPaddingTop: Int,
    val playVideoIconPaddingBottom: Int,
    val playVideoIconPaddingStart: Int,
    val playVideoIconPaddingEnd: Int,
    val imagePlaceholder: Drawable?,
) : ViewStyle {
    internal companion object {

        operator fun invoke(context: Context, attrs: AttributeSet?): MediaAttachmentGridViewStyle {
            context.obtainStyledAttributes(
                attrs,
                R.styleable.MediaAttachmentGridView,
                R.attr.ermisUiMediaAttachmentGridViewStyle,
                R.style.ermisUi_MediaAttachmentGridView,
            ).let { styledAttributes ->
                val style = MediaAttachmentGridViewStyle(context, styledAttributes)
                styledAttributes.recycle()
                return style
            }
        }

        operator fun invoke(context: Context, it: TypedArray): MediaAttachmentGridViewStyle {
            val showUserAvatars =
                it.getBoolean(
                    R.styleable.MediaAttachmentGridView_ermisUiMediaAttachmentGridViewShowUserAvatars,
                    true,
                )

            val playVideoButtonIcon = it.getDrawable(
                R.styleable.MediaAttachmentGridView_ermisUiMediaAttachmentGridViewPlayVideoButtonIcon,
            ) ?: context.getDrawableCompat(R.drawable.ic_play)

            val playVideoIconTint = it.getColorOrNull(
                R.styleable.MediaAttachmentGridView_ermisUiMediaAttachmentGridViewPlayVideoIconTint,
            )

            val playVideoIconBackgroundColor =
                it.getColor(
                    R.styleable.MediaAttachmentGridView_ermisUiMediaAttachmentGridViewPlayVideoIconBackgroundColor,
                    context.getColorCompat(R.color.ui_literal_white),
                )

            val playVideoIconCornerRadius =
                it.getDimension(
                    R.styleable.MediaAttachmentGridView_ermisUiMediaAttachmentGridViewPlayVideoIconCornerRadius,
                    0f,
                )

            val playVideoIconElevation =
                it.getDimension(
                    R.styleable.MediaAttachmentGridView_ermisUiMediaAttachmentGridViewPlayVideoIconElevation,
                    0f,
                )

            val playVideoIconPaddingTop =
                it.getDimensionPixelSize(
                    R.styleable.MediaAttachmentGridView_ermisUiMediaAttachmentGridViewPlayVideoIconPaddingTop,
                    0,
                )

            val playVideoIconPaddingBottom =
                it.getDimensionPixelSize(
                    R.styleable.MediaAttachmentGridView_ermisUiMediaAttachmentGridViewPlayVideoIconPaddingBottom,
                    0,
                )

            val playVideoIconPaddingStart =
                it.getDimensionPixelSize(
                    R.styleable.MediaAttachmentGridView_ermisUiMediaAttachmentGridViewPlayVideoIconPaddingStart,
                    0,
                )

            val playVideoIconPaddingEnd =
                it.getDimensionPixelSize(
                    R.styleable.MediaAttachmentGridView_ermisUiMediaAttachmentGridViewPlayVideoIconPaddingEnd,
                    0,
                )

            val playVideoIconPadding =
                it.getDimensionOrNull(
                    R.styleable.MediaAttachmentGridView_ermisUiMediaAttachmentGridViewPlayVideoIconPadding,
                )?.toInt()

            val imagePlaceholder = it.getDrawable(
                R.styleable.MediaAttachmentGridView_ermisUiMediaAttachmentGridViewImagePlaceholder,
            ) ?: ContextCompat.getDrawable(
                context,
                R.drawable.picture_placeholder,
            )

            return MediaAttachmentGridViewStyle(
                showUserAvatars = showUserAvatars,
                playVideoButtonIcon = playVideoButtonIcon,
                playVideoIconTint = playVideoIconTint,
                playVideoIconBackgroundColor = playVideoIconBackgroundColor,
                playVideoIconCornerRadius = playVideoIconCornerRadius,
                playVideoIconElevation = playVideoIconElevation,
                playVideoIconPaddingTop = playVideoIconPadding ?: playVideoIconPaddingTop,
                playVideoIconPaddingBottom = playVideoIconPadding ?: playVideoIconPaddingBottom,
                playVideoIconPaddingStart = playVideoIconPadding ?: playVideoIconPaddingStart,
                playVideoIconPaddingEnd = playVideoIconPadding ?: playVideoIconPaddingEnd,
                imagePlaceholder = imagePlaceholder,
            ).let(TransformStyle.mediaAttachmentGridViewStyle::transform)
        }
    }
}
