
package network.ermis.ui.view.messages.adapter.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.ColorInt
import network.ermis.ui.components.R
import network.ermis.ui.common.utils.GiphyInfoType
import network.ermis.ui.common.utils.GiphySizingMode
import network.ermis.ui.utils.extensions.getColorCompat
import network.ermis.ui.utils.extensions.getDrawableCompat
import network.ermis.ui.utils.extensions.getEnum
import network.ermis.ui.utils.extensions.use

/**
 * Sets the style for [io.getstream.chat.android.ui.feature.messages.list.adapter.view.internal.GiphyMediaAttachmentView]
 * by obtaining styled attributes.
 *
 * @param progressIcon Displayed while the Giphy is Loading.
 * @param giphyIcon Displays the Giphy logo over the Giphy image.
 * @param placeholderIcon Displayed while the Giphy is Loading.
 * @param imageBackgroundColor Sets the background colour for the Giphy container.
 * @param giphyType Sets the Giphy type which directly affects image quality and if the container is resized or not.
 * @param scaleType Sets the scaling type for loading the image. E.g. 'centerCrop', 'fitCenter', etc...
 * @param sizingMode Sets the way the Giphy container scales itself, either adaptive or of a fixed size.
 * @param width Sets the width of the Giphy container. This value is ignored if Giphys are adaptively sized.
 * @param height Sets the height of the Giphy container. This value is ignored if Giphys are adaptively sized.
 * @param dimensionRatio Sets the dimension ratio of the Giphy container. This value is ignored if
 * Giphys are adaptively sized.
 */
public class GiphyMediaAttachmentViewStyle(
    public val progressIcon: Drawable,
    public val giphyIcon: Drawable,
    public val placeholderIcon: Drawable,
    @ColorInt public val imageBackgroundColor: Int,
    public val giphyType: GiphyInfoType,
    public val scaleType: ImageView.ScaleType,
    public val sizingMode: GiphySizingMode,
    public val width: Int,
    public val height: Int,
    public val dimensionRatio: Float,
) {
    public companion object {
        /**
         * Fetches styled attributes and returns them wrapped inside of [GiphyMediaAttachmentViewStyle].
         */
        internal operator fun invoke(context: Context, attrs: AttributeSet?): GiphyMediaAttachmentViewStyle {
            context.obtainStyledAttributes(
                attrs,
                R.styleable.GiphyMediaAttachmentView,
                R.attr.ermisUiMessageListGiphyAttachmentStyle,
                R.style.ermisUi_MessageList_GiphyMediaAttachment,
            ).use { attributes ->
                val progressIcon =
                    attributes.getDrawable(R.styleable.GiphyMediaAttachmentView_ermisUiGiphyMediaAttachmentProgressIcon)
                        ?: context.getDrawableCompat(R.drawable.rotating_indeterminate_progress_gradient)!!

                val giphyIcon =
                    attributes.getDrawable(R.styleable.GiphyMediaAttachmentView_ermisUiGiphyMediaAttachmentGiphyIcon)
                        ?: context.getDrawableCompat(R.drawable.giphy_label)!!

                val imageBackgroundColor = attributes.getColor(
                    R.styleable.GiphyMediaAttachmentView_ermisUiGiphyMediaAttachmentImageBackgroundColor,
                    context.getColorCompat(R.color.ui_grey),
                )

                val placeholderIcon =
                    attributes.getDrawable(R.styleable.GiphyMediaAttachmentView_ermisUiGiphyMediaAttachmentPlaceHolderIcon)
                        ?: context.getDrawableCompat(R.drawable.picture_placeholder)!!

                val giphyType =
                    attributes.getEnum(
                        R.styleable.GiphyMediaAttachmentView_ermisUiGiphyMediaAttachmentGiphyType,
                        GiphyInfoType.FIXED_HEIGHT,
                    )

                val scaleType =
                    attributes.getEnum(
                        R.styleable.GiphyMediaAttachmentView_ermisUiGiphyMediaAttachmentScaleType,
                        ImageView.ScaleType.FIT_CENTER,
                    )

                val sizingMode = attributes.getEnum(
                    R.styleable.GiphyMediaAttachmentView_ermisUiGiphyMediaAttachmentSizingMode,
                    GiphySizingMode.ADAPTIVE,
                )

                val width =
                    attributes.getLayoutDimension(
                        R.styleable.GiphyMediaAttachmentView_ermisUiGiphyMediaAttachmentWidth,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                    )

                val height =
                    attributes.getLayoutDimension(
                        R.styleable.GiphyMediaAttachmentView_ermisUiGiphyMediaAttachmentHeight,
                        NO_GIVEN_HEIGHT,
                    )

                val dimensionRatio =
                    attributes.getFloat(
                        R.styleable.GiphyMediaAttachmentView_ermisUiGiphyMediaAttachmentDimensionRatio,
                        SQUARE_DIMENSION_RATIO,
                    )

                return GiphyMediaAttachmentViewStyle(
                    progressIcon = progressIcon,
                    giphyIcon = giphyIcon,
                    placeholderIcon = placeholderIcon,
                    imageBackgroundColor = imageBackgroundColor,
                    giphyType = giphyType,
                    scaleType = scaleType,
                    sizingMode = sizingMode,
                    width = width,
                    height = height,
                    dimensionRatio = dimensionRatio,
                )
            }
        }

        /**
         * Signifies that the user has not set a dimension ratio.
         */
        public const val NO_GIVEN_DIMENSION_RATIO: Float = -1f

        /**
         * Signifies that the user has not set height.
         * Used to set the initial condition.
         */
        public const val NO_GIVEN_HEIGHT: Int = -1

        /**
         * A dimension ratios that gives an equal height as width,
         * hence creating a square appearance.
         */
        public const val SQUARE_DIMENSION_RATIO: Float = 1f
    }
}
