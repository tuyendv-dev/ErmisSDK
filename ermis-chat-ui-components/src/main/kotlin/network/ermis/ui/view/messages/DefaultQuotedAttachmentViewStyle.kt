
package network.ermis.ui.view.messages

import android.content.Context
import androidx.annotation.Px
import network.ermis.ui.components.R
import network.ermis.ui.helper.TransformStyle
import network.ermis.ui.helper.ViewStyle
import network.ermis.ui.utils.extensions.getDimension

/**
 * Style to be applied to [DefaultQuotedAttachmentView].
 * Use [TransformStyle.defaultQuotedAttachmentViewStyleTransformer] to change the style programmatically.
 *
 * @param fileAttachmentHeight The height of the quoted file attachment.
 * @param fileAttachmentWidth The width of the quoted file attachment.
 * @param imageAttachmentHeight The width of the quoted image attachment.
 * @param imageAttachmentWidth The height of the quoted image attachment.
 * @param quotedImageRadius The radius of the quoted attachment corners.
 */
public class DefaultQuotedAttachmentViewStyle(
    @Px public val fileAttachmentHeight: Int,
    @Px public val fileAttachmentWidth: Int,
    @Px public val imageAttachmentHeight: Int,
    @Px public val imageAttachmentWidth: Int,
    @Px public val quotedImageRadius: Int,
) : ViewStyle {

    internal companion object {
        operator fun invoke(context: Context): DefaultQuotedAttachmentViewStyle {
            val fileAttachmentHeight: Int = context.getDimension(R.dimen.ermisUiQuotedFileAttachmentViewHeight)
            val fileAttachmentWidth: Int = context.getDimension(R.dimen.ermisUiQuotedFileAttachmentViewWidth)
            val imageAttachmentHeight: Int = context.getDimension(R.dimen.ermisUiQuotedImageAttachmentViewHeight)
            val imageAttachmentWidth: Int = context.getDimension(R.dimen.ermisUiQuotedImageAttachmentViewWidth)
            val quotedImageRadius: Int = context.getDimension(R.dimen.ermisUiQuotedImageAttachmentImageRadius)

            return DefaultQuotedAttachmentViewStyle(
                fileAttachmentHeight = fileAttachmentHeight,
                fileAttachmentWidth = fileAttachmentWidth,
                imageAttachmentHeight = imageAttachmentHeight,
                imageAttachmentWidth = imageAttachmentWidth,
                quotedImageRadius = quotedImageRadius,
            ).let(TransformStyle.defaultQuotedAttachmentViewStyleTransformer::transform)
        }
    }
}
