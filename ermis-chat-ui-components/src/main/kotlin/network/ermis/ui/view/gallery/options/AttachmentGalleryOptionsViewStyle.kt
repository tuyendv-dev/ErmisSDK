
package network.ermis.ui.view.gallery.options

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.core.content.res.ResourcesCompat
import network.ermis.ui.components.R
import network.ermis.ui.view.gallery.AttachmentGalleryActivity
import network.ermis.ui.font.TextStyle
import network.ermis.ui.helper.TransformStyle
import network.ermis.ui.helper.ViewStyle
import network.ermis.ui.utils.extensions.getColorCompat
import network.ermis.ui.utils.extensions.getDimension
import network.ermis.ui.utils.extensions.getDrawableCompat
import network.ermis.ui.utils.extensions.use

/**
 * Controls how video attachments are displayed inside of the [AttachmentGalleryActivity].
 *
 * @param optionTextStyle The text style of each option.
 * @param backgroundColor The background color of the options dialog.
 * @param replyOptionEnabled If the "reply" option is present in the list.
 * @param replyOptionDrawable  The icon to the "reply" option.
 * @param showInChatOptionEnabled If the "show in chat" option present in the list.
 * @param showInChatOptionDrawable The icon for the "show in chat" option.
 * @param saveMediaOptionEnabled If the "save media" option is present in the list.
 * @param saveMediaOptionDrawable The icon for the "save media" option.
 * @param deleteOptionEnabled If the "delete" option is present in the list.
 * @param deleteOptionDrawable The icon for the "delete" option.
 * @param deleteOptionTextColor The text color of the "delete" option.
 */
public data class AttachmentGalleryOptionsViewStyle(
    val optionTextStyle: TextStyle,
    @ColorInt val backgroundColor: Int,
    val replyOptionEnabled: Boolean,
    val replyOptionDrawable: Drawable,
    val showInChatOptionEnabled: Boolean,
    val showInChatOptionDrawable: Drawable,
    val saveMediaOptionEnabled: Boolean,
    val saveMediaOptionDrawable: Drawable,
    val deleteOptionEnabled: Boolean,
    val deleteOptionDrawable: Drawable,
    @ColorInt val deleteOptionTextColor: Int,
) : ViewStyle {

    internal companion object {
        operator fun invoke(context: Context, attrs: AttributeSet?): AttachmentGalleryOptionsViewStyle {
            context.obtainStyledAttributes(
                attrs,
                R.styleable.AttachmentOptionsView,
                R.attr.ermisUiAttachmentGalleryOptionsStyle,
                R.style.ermisUi_AttachmentGallery_Options,
            ).use {
                return AttachmentGalleryOptionsViewStyle(context, it)
            }
        }

        operator fun invoke(context: Context, it: TypedArray): AttachmentGalleryOptionsViewStyle {
            val optionTextStyle = TextStyle.Builder(it)
                .size(
                    R.styleable.AttachmentOptionsView_ermisUiAttachmentOptionTextSize,
                    context.getDimension(R.dimen.ui_text_medium),
                )
                .color(
                    R.styleable.AttachmentOptionsView_ermisUiAttachmentOptionTextColor,
                    context.getColorCompat(R.color.ui_text_color_primary),
                )
                .font(
                    R.styleable.AttachmentOptionsView_ermisUiAttachmentOptionTextFontAssets,
                    R.styleable.AttachmentOptionsView_ermisUiAttachmentOptionTextFont,
                    ResourcesCompat.getFont(context, R.font.roboto_medium) ?: Typeface.DEFAULT,
                )
                .style(
                    R.styleable.AttachmentOptionsView_ermisUiAttachmentOptionTextStyle,
                    Typeface.NORMAL,
                )
                .build()

            val backgroundColor = it.getColor(
                R.styleable.AttachmentOptionsView_ermisUiAttachmentOptionsBackgroundColor,
                context.getColorCompat(R.color.ui_white_snow),
            )

            val replyOptionEnabled = it.getBoolean(
                R.styleable.AttachmentOptionsView_ermisUiAttachmentReplyEnabled,
                true,
            )

            val replyOptionDrawable = it.getDrawable(
                R.styleable.AttachmentOptionsView_ermisUiReplyIcon,
            ) ?: context.getDrawableCompat(R.drawable.ic_arrow_curve_left_grey)!!

            val showInChatOptionEnabled = it.getBoolean(
                R.styleable.AttachmentOptionsView_ermisUiShowInChatEnabled,
                true,
            )

            val showInChatOptionDrawable = it.getDrawable(
                R.styleable.AttachmentOptionsView_ermisUiShowInChatIcon,
            ) ?: context.getDrawableCompat(R.drawable.ic_show_in_chat)!!

            val saveMediaOptionEnabled = it.getBoolean(
                R.styleable.AttachmentOptionsView_ermisUiSaveMediaEnabled,
                true,
            )

            val saveMediaOptionDrawable = it.getDrawable(
                R.styleable.AttachmentOptionsView_ermisUiSaveMediaIcon,
            ) ?: context.getDrawableCompat(R.drawable.ic_download)!!

            val deleteOptionEnabled = it.getBoolean(
                R.styleable.AttachmentOptionsView_ermisUiDeleteEnabled,
                true,
            )

            val deleteOptionDrawable = it.getDrawable(
                R.styleable.AttachmentOptionsView_ermisUiDeleteIcon,
            ) ?: context.getDrawableCompat(R.drawable.ic_delete)!!

            val deleteOptionTextColor = it.getColor(
                R.styleable.AttachmentOptionsView_ermisUiDeleteTextTint,
                context.getColorCompat(R.color.ui_accent_red),
            )

            return AttachmentGalleryOptionsViewStyle(
                optionTextStyle = optionTextStyle,
                backgroundColor = backgroundColor,
                replyOptionEnabled = replyOptionEnabled,
                replyOptionDrawable = replyOptionDrawable,
                showInChatOptionEnabled = showInChatOptionEnabled,
                showInChatOptionDrawable = showInChatOptionDrawable,
                saveMediaOptionEnabled = saveMediaOptionEnabled,
                saveMediaOptionDrawable = saveMediaOptionDrawable,
                deleteOptionEnabled = deleteOptionEnabled,
                deleteOptionDrawable = deleteOptionDrawable,
                deleteOptionTextColor = deleteOptionTextColor,
            ).let(TransformStyle.attachmentGalleryOptionsStyleTransformer::transform)
        }
    }
}
