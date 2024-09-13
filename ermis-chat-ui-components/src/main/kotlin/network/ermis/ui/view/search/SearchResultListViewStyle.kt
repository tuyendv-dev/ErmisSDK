
package network.ermis.ui.view.search

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

/**
 * Style for [SearchResultListView].
 * Use this class together with [TransformStyle.searchResultListViewStyleTransformer] to change [SearchResultListView] styles programmatically.
 *
 * @property backgroundColor Background color for search results list. Default value is [R.color.ui_white].
 * @property searchInfoBarBackground Background for search info bar. Default value is [R.drawable.bg_gradient].
 * @property searchInfoBarTextStyle Appearance for text displayed in search info bar.
 * @property emptyStateIcon Icon for empty state view. Default value is [R.drawable.ic_search_empty].
 * @property emptyStateTextStyle Appearance for empty state text.
 * @property progressBarIcon Animated progress drawable. Default value is [R.drawable.rotating_indeterminate_progress_gradient].
 * @property messagePreviewStyle Style for single search result item.
 */
public data class SearchResultListViewStyle(
    @ColorInt public val backgroundColor: Int,
    public val searchInfoBarBackground: Drawable,
    public val searchInfoBarTextStyle: TextStyle,
    public val emptyStateIcon: Drawable,
    public val emptyStateTextStyle: TextStyle,
    public val progressBarIcon: Drawable,
    public val messagePreviewStyle: MessagePreviewStyle,
) : ViewStyle {
    internal companion object {
        operator fun invoke(context: Context, attrs: AttributeSet?): SearchResultListViewStyle {
            context.obtainStyledAttributes(
                attrs,
                R.styleable.SearchResultListView,
                R.attr.ermisUiSearchResultListViewStyle,
                R.style.ermisUi_SearchResultListView,
            ).use { a ->
                val backgroundColor =
                    a.getColor(
                        R.styleable.SearchResultListView_ermisUiSearchResultListBackground,
                        context.getColorCompat(R.color.ui_white),
                    )

                val searchInfoBarBackground =
                    a.getDrawable(R.styleable.SearchResultListView_ermisUiSearchResultListSearchInfoBarBackground)
                        ?: context.getDrawableCompat(R.drawable.bg_gradient)!!
                val searchInfoBarTextStyle = TextStyle.Builder(a)
                    .size(
                        R.styleable.SearchResultListView_ermisUiSearchResultListSearchInfoBarTextSize,
                        context.getDimension(R.dimen.ui_text_small),
                    )
                    .color(
                        R.styleable.SearchResultListView_ermisUiSearchResultListSearchInfoBarTextColor,
                        context.getColorCompat(R.color.ui_text_color_primary),
                    )
                    .font(
                        R.styleable.SearchResultListView_ermisUiSearchResultListSearchInfoBarTextFontAssets,
                        R.styleable.SearchResultListView_ermisUiSearchResultListSearchInfoBarTextFont,
                    )
                    .style(
                        R.styleable.SearchResultListView_ermisUiSearchResultListSearchInfoBarTextStyle,
                        Typeface.NORMAL,
                    )
                    .build()

                val emptyStateIcon =
                    a.getDrawable(R.styleable.SearchResultListView_ermisUiSearchResultListEmptyStateIcon)
                        ?: context.getDrawableCompat(R.drawable.ic_search_empty)!!
                val emptyStateTextStyle = TextStyle.Builder(a)
                    .size(
                        R.styleable.SearchResultListView_ermisUiSearchResultListEmptyStateTextSize,
                        context.getDimension(R.dimen.ui_text_medium),
                    )
                    .color(
                        R.styleable.SearchResultListView_ermisUiSearchResultListEmptyStateTextColor,
                        context.getColorCompat(R.color.ui_text_color_secondary),
                    )
                    .font(
                        R.styleable.SearchResultListView_ermisUiSearchResultListEmptyStateTextFontAssets,
                        R.styleable.SearchResultListView_ermisUiSearchResultListEmptyStateTextFont,
                    )
                    .style(
                        R.styleable.SearchResultListView_ermisUiSearchResultListEmptyStateTextStyle,
                        Typeface.NORMAL,
                    )
                    .build()

                val progressBarIcon =
                    a.getDrawable(R.styleable.SearchResultListView_ermisUiSearchResultListProgressBarIcon)
                        ?: context.getDrawableCompat(R.drawable.rotating_indeterminate_progress_gradient)!!

                val senderTextStyle = TextStyle.Builder(a)
                    .size(
                        R.styleable.SearchResultListView_ermisUiSearchResultListSenderNameTextSize,
                        context.getDimension(R.dimen.ermis_ui_channel_item_title),
                    )
                    .color(
                        R.styleable.SearchResultListView_ermisUiSearchResultListSenderNameTextColor,
                        context.getColorCompat(R.color.ui_text_color_primary),
                    )
                    .font(
                        R.styleable.SearchResultListView_ermisUiSearchResultListSenderNameTextFontAssets,
                        R.styleable.SearchResultListView_ermisUiSearchResultListSenderNameTextFont,
                    )
                    .style(
                        R.styleable.SearchResultListView_ermisUiSearchResultListSenderNameTextStyle,
                        Typeface.NORMAL,
                    )
                    .build()

                val messageTextStyle = TextStyle.Builder(a)
                    .size(
                        R.styleable.SearchResultListView_ermisUiSearchResultListMessageTextSize,
                        context.getDimension(R.dimen.ermis_ui_channel_item_message),
                    )
                    .color(
                        R.styleable.SearchResultListView_ermisUiSearchResultListMessageTextColor,
                        context.getColorCompat(R.color.ui_text_color_secondary),
                    )
                    .font(
                        R.styleable.SearchResultListView_ermisUiSearchResultListMessageTextFontAssets,
                        R.styleable.SearchResultListView_ermisUiSearchResultListMessageTextFont,
                    )
                    .style(
                        R.styleable.SearchResultListView_ermisUiSearchResultListMessageTextStyle,
                        Typeface.NORMAL,
                    )
                    .build()

                val messageTimeTextStyle = TextStyle.Builder(a)
                    .size(
                        R.styleable.SearchResultListView_ermisUiSearchResultListMessageTimeTextSize,
                        context.getDimension(R.dimen.ermis_ui_channel_item_message),
                    )
                    .color(
                        R.styleable.SearchResultListView_ermisUiSearchResultListMessageTimeTextColor,
                        context.getColorCompat(R.color.ui_text_color_secondary),
                    )
                    .font(
                        R.styleable.SearchResultListView_ermisUiSearchResultListMessageTimeTextFontAssets,
                        R.styleable.SearchResultListView_ermisUiSearchResultListMessageTimeTextFont,
                    )
                    .style(
                        R.styleable.SearchResultListView_ermisUiSearchResultListMessageTimeTextStyle,
                        Typeface.NORMAL,
                    )
                    .build()

                return SearchResultListViewStyle(
                    backgroundColor = backgroundColor,
                    searchInfoBarBackground = searchInfoBarBackground,
                    searchInfoBarTextStyle = searchInfoBarTextStyle,
                    emptyStateIcon = emptyStateIcon,
                    emptyStateTextStyle = emptyStateTextStyle,
                    progressBarIcon = progressBarIcon,
                    messagePreviewStyle = MessagePreviewStyle(
                        messageSenderTextStyle = senderTextStyle,
                        messageTextStyle = messageTextStyle,
                        messageTimeTextStyle = messageTimeTextStyle,
                    ),
                ).let(TransformStyle.searchResultListViewStyleTransformer::transform)
            }
        }
    }
}
