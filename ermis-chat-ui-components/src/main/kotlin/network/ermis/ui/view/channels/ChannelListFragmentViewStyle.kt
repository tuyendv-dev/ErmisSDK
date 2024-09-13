
package network.ermis.ui.view.channels

import android.content.Context
import androidx.annotation.Px
import network.ermis.ui.components.R
import network.ermis.ui.helper.TransformStyle
import network.ermis.ui.helper.ViewStyle
import network.ermis.ui.utils.extensions.getDimension

/**
 * Style for [ChannelListFragment].
 * Use this class together with [TransformStyle.channelListFragmentStyleTransformer] to change [ChannelListFragment] styles programmatically.
 *
 * @property searchInputMarginStart The start margin of the search input.
 * @property searchInputMarginTop The top margin of the search input.
 * @property searchInputMarginEnd The end margin of the search input.
 * @property searchInputMarginBottom The bottom margin of the search input.
 */
public data class ChannelListFragmentViewStyle(
    @Px public val searchInputMarginStart: Int,
    @Px public val searchInputMarginTop: Int,
    @Px public val searchInputMarginEnd: Int,
    @Px public val searchInputMarginBottom: Int,
) : ViewStyle {

    internal companion object {
        operator fun invoke(context: Context): ChannelListFragmentViewStyle {
            val searchInputMarginTop: Int = context.getDimension(R.dimen.ui_channel_list_search_margin_top)
            val searchInputMarginStart: Int = context.getDimension(R.dimen.ui_channel_list_search_margin_start)
            val searchInputMarginEnd: Int = context.getDimension(R.dimen.ui_channel_list_search_margin_end)
            val searchInputMarginBottom: Int = context.getDimension(R.dimen.ui_channel_list_search_margin_bottom)

            return ChannelListFragmentViewStyle(
                searchInputMarginStart = searchInputMarginStart,
                searchInputMarginTop = searchInputMarginTop,
                searchInputMarginEnd = searchInputMarginEnd,
                searchInputMarginBottom = searchInputMarginBottom,
            ).let(TransformStyle.channelListFragmentStyleTransformer::transform)
        }
    }
}
