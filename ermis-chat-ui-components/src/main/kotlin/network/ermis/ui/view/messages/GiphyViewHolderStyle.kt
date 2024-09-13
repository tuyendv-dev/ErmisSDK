
package network.ermis.ui.view.messages

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.core.content.res.ResourcesCompat
import network.ermis.ui.components.R
import network.ermis.ui.view.messages.adapter.viewholder.decorator.impl.GiphyViewHolder
import network.ermis.ui.font.TextStyle
import network.ermis.ui.helper.TransformStyle
import network.ermis.ui.helper.ViewStyle
import network.ermis.ui.utils.extensions.getColorCompat
import network.ermis.ui.utils.extensions.getDimension
import network.ermis.ui.utils.extensions.getDrawableCompat

/**
 * Style for [GiphyViewHolder].
 * Use this class together with [TransformStyle.giphyViewHolderStyleTransformer] to change styles programmatically.
 *
 * @param cardBackgroundColor Card's background color. Default value is [R.color.ui_white].
 * @param cardElevation Card's elevation. Default value is [R.dimen.ui_elevation_small].
 * @param cardButtonDividerColor Color for dividers placed between action buttons. Default value is [R.color.ui_border].
 * @param giphyIcon Giphy icon. Default value is [R.drawable.ic_giphy].
 * @param labelTextStyle Appearance for label text.
 * @param queryTextStyle Appearance for query text.
 * @param cancelButtonTextStyle Appearance for cancel button text.
 * @param shuffleButtonTextStyle Appearance for shuffle button text.
 * @param sendButtonTextStyle Appearance for send button text.
 */
public data class GiphyViewHolderStyle(
    @ColorInt val cardBackgroundColor: Int,
    @Px val cardElevation: Float,
    @ColorInt val cardButtonDividerColor: Int,
    val giphyIcon: Drawable,
    val labelTextStyle: TextStyle,
    val queryTextStyle: TextStyle,
    val cancelButtonTextStyle: TextStyle,
    val shuffleButtonTextStyle: TextStyle,
    val sendButtonTextStyle: TextStyle,
) : ViewStyle {

    internal companion object {
        operator fun invoke(context: Context, attributes: TypedArray): GiphyViewHolderStyle {
            val boldTypeface = ResourcesCompat.getFont(context, R.font.roboto_bold) ?: Typeface.DEFAULT_BOLD
            val mediumTypeface = ResourcesCompat.getFont(context, R.font.roboto_medium) ?: Typeface.DEFAULT

            val cardBackgroundColor = attributes.getColor(
                R.styleable.MessageListView_ermisUiGiphyCardBackgroundColor,
                context.getColorCompat(R.color.ui_white),
            )
            val cardElevation = attributes.getDimension(
                R.styleable.MessageListView_ermisUiGiphyCardElevation,
                context.getDimension(R.dimen.ui_elevation_small).toFloat(),
            )

            val cardButtonDividerColor = attributes.getColor(
                R.styleable.MessageListView_ermisUiGiphyCardButtonDividerColor,
                context.getColorCompat(R.color.ui_border),
            )

            val giphyIcon = attributes.getDrawable(R.styleable.MessageListView_ermisUiGiphyIcon)
                ?: context.getDrawableCompat(R.drawable.ic_giphy)!!

            val labelTextStyle = TextStyle.Builder(attributes)
                .size(
                    R.styleable.MessageListView_ermisUiGiphyLabelTextSize,
                    context.getDimension(R.dimen.ui_text_medium),
                )
                .color(
                    R.styleable.MessageListView_ermisUiGiphyLabelTextColor,
                    context.getColorCompat(R.color.ui_text_color_primary),
                )
                .font(
                    R.styleable.MessageListView_ermisUiGiphyLabelTextFontAssets,
                    R.styleable.MessageListView_ermisUiGiphyLabelTextFont,
                    boldTypeface,
                )
                .style(
                    R.styleable.MessageListView_ermisUiGiphyLabelTextStyle,
                    Typeface.NORMAL,
                )
                .build()

            val queryTextStyle = TextStyle.Builder(attributes)
                .size(
                    R.styleable.MessageListView_ermisUiGiphyQueryTextSize,
                    context.getDimension(R.dimen.ui_text_medium),
                )
                .color(
                    R.styleable.MessageListView_ermisUiGiphyQueryTextColor,
                    context.getColorCompat(R.color.ui_text_color_primary),
                )
                .font(
                    R.styleable.MessageListView_ermisUiGiphyQueryTextFontAssets,
                    R.styleable.MessageListView_ermisUiGiphyQueryTextFont,
                    mediumTypeface,
                )
                .style(
                    R.styleable.MessageListView_ermisUiGiphyQueryTextStyle,
                    Typeface.NORMAL,
                )
                .build()

            val cancelButtonTextStyle = TextStyle.Builder(attributes)
                .size(
                    R.styleable.MessageListView_ermisUiGiphyCancelButtonTextSize,
                    context.getDimension(R.dimen.ui_text_medium),
                )
                .color(
                    R.styleable.MessageListView_ermisUiGiphyCancelButtonTextColor,
                    context.getColorCompat(R.color.ui_text_color_secondary),
                )
                .font(
                    R.styleable.MessageListView_ermisUiGiphyCancelButtonTextFontAssets,
                    R.styleable.MessageListView_ermisUiGiphyCancelButtonTextFont,
                    boldTypeface,
                )
                .style(
                    R.styleable.MessageListView_ermisUiGiphyCancelButtonTextStyle,
                    Typeface.NORMAL,
                )
                .build()

            val shuffleButtonTextStyle = TextStyle.Builder(attributes)
                .size(
                    R.styleable.MessageListView_ermisUiGiphyShuffleButtonTextSize,
                    context.getDimension(R.dimen.ui_text_medium),
                )
                .color(
                    R.styleable.MessageListView_ermisUiGiphyShuffleButtonTextColor,
                    context.getColorCompat(R.color.ui_text_color_secondary),
                )
                .font(
                    R.styleable.MessageListView_ermisUiGiphyShuffleButtonTextFontAssets,
                    R.styleable.MessageListView_ermisUiGiphyShuffleButtonTextFont,
                    boldTypeface,
                )
                .style(
                    R.styleable.MessageListView_ermisUiGiphyShuffleButtonTextStyle,
                    Typeface.NORMAL,
                )
                .build()

            val sendButtonTextStyle = TextStyle.Builder(attributes)
                .size(
                    R.styleable.MessageListView_ermisUiGiphySendButtonTextSize,
                    context.getDimension(R.dimen.ui_text_medium),
                )
                .color(
                    R.styleable.MessageListView_ermisUiGiphySendButtonTextColor,
                    context.getColorCompat(R.color.ui_accent_blue),
                )
                .font(
                    R.styleable.MessageListView_ermisUiGiphySendButtonTextFontAssets,
                    R.styleable.MessageListView_ermisUiGiphySendButtonTextFont,
                    boldTypeface,
                )
                .style(
                    R.styleable.MessageListView_ermisUiGiphySendButtonTextStyle,
                    Typeface.NORMAL,
                )
                .build()

            return GiphyViewHolderStyle(
                cardBackgroundColor = cardBackgroundColor,
                cardElevation = cardElevation,
                cardButtonDividerColor = cardButtonDividerColor,
                giphyIcon = giphyIcon,
                labelTextStyle = labelTextStyle,
                queryTextStyle = queryTextStyle,
                cancelButtonTextStyle = cancelButtonTextStyle,
                shuffleButtonTextStyle = shuffleButtonTextStyle,
                sendButtonTextStyle = sendButtonTextStyle,
            ).let(TransformStyle.giphyViewHolderStyleTransformer::transform)
        }
    }
}
