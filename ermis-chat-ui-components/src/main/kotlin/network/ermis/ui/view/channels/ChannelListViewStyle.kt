
package network.ermis.ui.view.channels

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.annotation.LayoutRes
import androidx.annotation.Px
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.res.ResourcesCompat
import network.ermis.ui.components.R
import network.ermis.ui.view.channels.adapter.viewholder.internal.ChannelViewHolder
import network.ermis.ui.font.TextStyle
import network.ermis.ui.helper.TransformStyle
import network.ermis.ui.helper.ViewStyle
import network.ermis.ui.utils.extensions.getColorCompat
import network.ermis.ui.utils.extensions.getColorOrNull
import network.ermis.ui.utils.extensions.getDimension
import network.ermis.ui.utils.extensions.getDrawableCompat
import network.ermis.ui.utils.extensions.use

/**
 * Style for [ChannelListView].
 * Use this class together with [TransformStyle.channelListStyleTransformer] to change [ChannelListView] styles programmatically.
 *
 * @property optionsIcon Icon for channel's options. Default value is [R.drawable.ic_more].
 * @property deleteIcon Icon for deleting channel option. Default value is [R.drawable.ic_delete].
 * @property optionsEnabled Enables/disables channel's options. Enabled by default.
 * @property deleteEnabled Enables/disables delete channel option. Enabled by default.
 * @property swipeEnabled Enables/disables swipe on channel list item. Enabled by default.
 * @property backgroundLayoutColor Background color for [ChannelViewHolder]. Default value is [R.color.ui_white_smoke].
 * @property channelTitleText Appearance for channel's title, displayed in [ChannelViewHolder].
 * @property lastMessageText Appearance for last message text, displayed in [ChannelViewHolder].
 * @property lastMessageDateText Appearance for last message date text displayed in [ChannelViewHolder].
 * @property indicatorSentIcon Icon for indicating message sent status in [ChannelViewHolder]. Default value is [R.drawable.ic_check_single].
 * @property indicatorReadIcon Icon for indicating message read status in [ChannelViewHolder]. Default value is [R.drawable.ic_check_double].
 * @property indicatorPendingSyncIcon Icon for indicating sync pending status in [ChannelViewHolder]. Default value is [R.drawable.ic_clock].
 * @property foregroundLayoutColor Foreground color for [ChannelViewHolder]. Default value is [R.color.ui_white_snow].
 * @property unreadMessageCounterText Appearance for message counter text, displayed in [ChannelViewHolder].
 * @property unreadMessageCounterBackgroundColor Background color for message counter, displayed in [ChannelViewHolder]. Default value is [R.color.ui_accent_red].
 * @property mutedChannelIcon Icon for muted channel, displayed in [ChannelViewHolder]. Default value is [R.drawable.ic_mute_black].
 * @property itemSeparator Items' separator. Default value is [R.drawable.divider].
 * @property loadingView Loading view. Default value is [R.layout.default_loading_view].
 * @property emptyStateView Empty state view. Default value is [R.layout.channel_list_empty_state_view].
 * @property loadingMoreView Loading more view. Default value is [R.layout.channel_list_loading_more_view].
 * @property edgeEffectColor Color applied to the [ChannelListView] edge effect. Pass null if you want to use default [android.R.attr.colorEdgeEffect]. Default value is null.
 * @property showChannelDeliveryStatusIndicator Flag if we need to show the delivery indicator or not.
 * @property readCountEnabled Enables/disables read count. Enabled by default.
 * @property itemHeight Height of the channel list item. Default value is [R.dimen.ui_channel_list_item_height].
 * @property itemMarginStart Start margin of the channel list item. Default value is [R.dimen.ui_channel_list_item_margin_start].
 * @property itemMarginEnd End margin of the channel list item. Default value is [R.dimen.ui_channel_list_item_margin_end].
 * @property itemTitleMarginStart Start margin of the channel list item title. Default value is [R.dimen.ui_channel_list_item_title_margin_start].
 * @property itemVerticalSpacerHeight Height of the channel list item vertical spacer. Default value is [R.dimen.ermis_ui_channel_list_item_vertical_spacer_height].
 * @property itemVerticalSpacerPosition Position of the channel list item vertical spacer. Default value is [R.dimen.ermis_ui_channel_list_item_vertical_spacer_position].
 */
public data class ChannelListViewStyle(
    public val optionsIcon: Drawable,
    public val deleteIcon: Drawable,
    public val optionsEnabled: Boolean,
    public val deleteEnabled: Boolean,
    public val swipeEnabled: Boolean,
    @ColorInt public val backgroundColor: Int,
    @ColorInt public val backgroundLayoutColor: Int,
    public val channelTitleText: TextStyle,
    public val lastMessageText: TextStyle,
    public val lastMessageDateText: TextStyle,
    public val indicatorSentIcon: Drawable,
    public val indicatorReadIcon: Drawable,
    public val indicatorPendingSyncIcon: Drawable,
    @ColorInt public val foregroundLayoutColor: Int,
    public val unreadMessageCounterText: TextStyle,
    @ColorInt public val unreadMessageCounterBackgroundColor: Int,
    public val mutedChannelIcon: Drawable,
    public val itemSeparator: Drawable,
    @LayoutRes public val loadingView: Int,
    @LayoutRes public val emptyStateView: Int,
    @LayoutRes public val loadingMoreView: Int,
    @ColorInt public val edgeEffectColor: Int?,
    public val showChannelDeliveryStatusIndicator: Boolean,
    public val readCountEnabled: Boolean,
    @Px public val itemHeight: Int,
    @Px public val itemMarginStart: Int,
    @Px public val itemMarginEnd: Int,
    @Px public val itemTitleMarginStart: Int,
    @Px public val itemVerticalSpacerHeight: Int,
    @Px public val itemVerticalSpacerPosition: Float,
) : ViewStyle {

    internal companion object {
        operator fun invoke(context: Context, attrs: AttributeSet?): ChannelListViewStyle {
            context.obtainStyledAttributes(
                attrs,
                R.styleable.ChannelListView,
                R.attr.ermisUiChannelListViewStyle,
                R.style.ermisUi_ChannelListView,
            ).use { a ->
                val optionsIcon = a.getDrawable(R.styleable.ChannelListView_ermisUiChannelOptionsIcon)
                    ?: context.getDrawableCompat(R.drawable.ic_more)!!

                val deleteIcon = a.getDrawable(R.styleable.ChannelListView_ermisUiChannelDeleteIcon)
                    ?: context.getDrawableCompat(R.drawable.ic_delete)!!

                val moreOptionsEnabled = a.getBoolean(
                    R.styleable.ChannelListView_ermisUiChannelOptionsEnabled,
                    false,
                )

                val deleteEnabled = a.getBoolean(
                    R.styleable.ChannelListView_ermisUiChannelDeleteEnabled,
                    true,
                )

                val swipeEnabled = a.getBoolean(
                    R.styleable.ChannelListView_ermisUiSwipeEnabled,
                    true,
                )

                val readCountEnabled = a.getBoolean(
                    R.styleable.ChannelListView_ermisUiReadCountEnabled,
                    true,
                )

                val backgroundColor = a.getColor(
                    R.styleable.ChannelListView_ermisUiChannelListBackgroundColor,
                    context.getColorCompat(R.color.ui_white),
                )

                val backgroundLayoutColor = a.getColor(
                    R.styleable.ChannelListView_ermisUiBackgroundLayoutColor,
                    context.getColorCompat(R.color.ui_white_smoke),
                )

                val channelTitleText = TextStyle.Builder(a)
                    .size(
                        R.styleable.ChannelListView_ermisUiChannelTitleTextSize,
                        context.getDimension(R.dimen.ermis_ui_channel_item_title),
                    )
                    .color(
                        R.styleable.ChannelListView_ermisUiChannelTitleTextColor,
                        context.getColorCompat(R.color.ui_text_color_primary),
                    )
                    .font(
                        R.styleable.ChannelListView_ermisUiChannelTitleFontAssets,
                        R.styleable.ChannelListView_ermisUiChannelTitleTextFont,
                    )
                    .style(
                        R.styleable.ChannelListView_ermisUiChannelTitleTextStyle,
                        Typeface.BOLD,
                    )
                    .build()

                val lastMessageText = TextStyle.Builder(a)
                    .size(
                        R.styleable.ChannelListView_ermisUiLastMessageTextSize,
                        context.getDimension(R.dimen.ermis_ui_channel_item_message),
                    )
                    .color(
                        R.styleable.ChannelListView_ermisUiLastMessageTextColor,
                        context.getColorCompat(R.color.ui_text_color_secondary),
                    )
                    .font(
                        R.styleable.ChannelListView_ermisUiLastMessageFontAssets,
                        R.styleable.ChannelListView_ermisUiLastMessageTextFont,
                    )
                    .style(
                        R.styleable.ChannelListView_ermisUiLastMessageTextStyle,
                        Typeface.NORMAL,
                    )
                    .build()

                val lastMessageDateText = TextStyle.Builder(a)
                    .size(
                        R.styleable.ChannelListView_ermisUiLastMessageDateTextSize,
                        context.getDimension(R.dimen.ermis_ui_channel_item_message_date),
                    )
                    .color(
                        R.styleable.ChannelListView_ermisUiLastMessageDateTextColor,
                        context.getColorCompat(R.color.ui_text_color_secondary),
                    )
                    .font(
                        R.styleable.ChannelListView_ermisUiLastMessageDateFontAssets,
                        R.styleable.ChannelListView_ermisUiLastMessageDateTextFont,
                    )
                    .style(
                        R.styleable.ChannelListView_ermisUiLastMessageDateTextStyle,
                        Typeface.NORMAL,
                    )
                    .build()

                val showChannelDeliveryStatusIndicator = a.getBoolean(
                    R.styleable.ChannelListView_ermisUiShowChannelDeliveryStatusIndicator,
                    true,
                )

                val indicatorSentIcon = a.getDrawable(R.styleable.ChannelListView_ermisUiIndicatorSentIcon)
                    ?: context.getDrawableCompat(R.drawable.ic_check_single)!!

                val indicatorReadIcon = a.getDrawable(R.styleable.ChannelListView_ermisUiIndicatorReadIcon)
                    ?: context.getDrawableCompat(R.drawable.ic_mark_as_read)!!

                val indicatorPendingSyncIcon =
                    a.getDrawableCompat(context, R.styleable.ChannelListView_ermisUiIndicatorPendingSyncIcon)
                        ?: AppCompatResources.getDrawable(context, R.drawable.ic_clock)!!

                val foregroundLayoutColor = a.getColor(
                    R.styleable.ChannelListView_ermisUiForegroundLayoutColor,
                    context.getColorCompat(R.color.ui_white_snow),
                )

                val unreadMessageCounterText = TextStyle.Builder(a)
                    .size(
                        R.styleable.ChannelListView_ermisUiUnreadMessageCounterTextSize,
                        context.getDimension(R.dimen.ui_text_small),
                    )
                    .color(
                        R.styleable.ChannelListView_ermisUiUnreadMessageCounterTextColor,
                        context.getColorCompat(R.color.ui_literal_white),
                    )
                    .font(
                        R.styleable.ChannelListView_ermisUiUnreadMessageCounterFontAssets,
                        R.styleable.ChannelListView_ermisUiUnreadMessageCounterTextFont,
                    )
                    .style(
                        R.styleable.ChannelListView_ermisUiUnreadMessageCounterTextStyle,
                        Typeface.NORMAL,
                    )
                    .build()

                val unreadMessageCounterBackgroundColor = a.getColor(
                    R.styleable.ChannelListView_ermisUiUnreadMessageCounterBackgroundColor,
                    context.getColorCompat(R.color.ui_accent_red),
                )

                val mutedChannelIcon = a.getDrawable(
                    R.styleable.ChannelListView_ermisUiMutedChannelIcon,
                ) ?: context.getDrawableCompat(R.drawable.ic_mute_black)!!

                val itemSeparator = a.getDrawable(
                    R.styleable.ChannelListView_ermisUiChannelsItemSeparatorDrawable,
                ) ?: context.getDrawableCompat(R.drawable.divider)!!

                val loadingView = a.getResourceId(
                    R.styleable.ChannelListView_ermisUiLoadingView,
                    R.layout.default_loading_view,
                )

                val emptyStateView = a.getResourceId(
                    R.styleable.ChannelListView_ermisUiEmptyStateView,
                    R.layout.channel_list_empty_state_view,
                )

                val loadingMoreView = a.getResourceId(
                    R.styleable.ChannelListView_ermisUiLoadingMoreView,
                    R.layout.channel_list_loading_more_view,
                )

                val edgeEffectColor = a.getColorOrNull(R.styleable.ChannelListView_ermisUiEdgeEffectColor)

                val itemHeight = a.getDimensionPixelSize(
                    R.styleable.ChannelListView_ermisUiChannelHeight,
                    context.getDimension(R.dimen.ui_channel_list_item_height),
                )

                val itemMarginStart = a.getDimensionPixelSize(
                    R.styleable.ChannelListView_ermisUiChannelMarginStart,
                    context.getDimension(R.dimen.ui_channel_list_item_margin_start),
                )

                val itemMarginEnd = a.getDimensionPixelSize(
                    R.styleable.ChannelListView_ermisUiChannelMarginEnd,
                    context.getDimension(R.dimen.ui_channel_list_item_margin_end),
                )

                val itemTitleMarginStart = a.getDimensionPixelSize(
                    R.styleable.ChannelListView_ermisUiChannelTitleMarginStart,
                    context.getDimension(R.dimen.ui_channel_list_item_title_margin_start),
                )

                val itemVerticalSpacerHeight = a.getDimensionPixelSize(
                    R.styleable.ChannelListView_ermisUiChannelVerticalSpacerHeight,
                    context.getDimension(R.dimen.ermis_ui_channel_list_item_vertical_spacer_height),
                )

                val itemVerticalSpacerPosition = a.getFloat(
                    R.styleable.ChannelListView_ermisUiChannelVerticalSpacerPosition,
                    ResourcesCompat.getFloat(
                        context.resources,
                        R.dimen.ermis_ui_channel_list_item_vertical_spacer_position,
                    ),
                )

                return ChannelListViewStyle(
                    optionsIcon = optionsIcon,
                    deleteIcon = deleteIcon,
                    optionsEnabled = moreOptionsEnabled,
                    deleteEnabled = deleteEnabled,
                    swipeEnabled = swipeEnabled,
                    backgroundColor = backgroundColor,
                    backgroundLayoutColor = backgroundLayoutColor,
                    channelTitleText = channelTitleText,
                    lastMessageText = lastMessageText,
                    lastMessageDateText = lastMessageDateText,
                    indicatorSentIcon = indicatorSentIcon,
                    indicatorReadIcon = indicatorReadIcon,
                    indicatorPendingSyncIcon = indicatorPendingSyncIcon,
                    foregroundLayoutColor = foregroundLayoutColor,
                    unreadMessageCounterText = unreadMessageCounterText,
                    unreadMessageCounterBackgroundColor = unreadMessageCounterBackgroundColor,
                    mutedChannelIcon = mutedChannelIcon,
                    itemSeparator = itemSeparator,
                    loadingView = loadingView,
                    emptyStateView = emptyStateView,
                    loadingMoreView = loadingMoreView,
                    edgeEffectColor = edgeEffectColor,
                    showChannelDeliveryStatusIndicator = showChannelDeliveryStatusIndicator,
                    readCountEnabled = readCountEnabled,
                    itemHeight = itemHeight,
                    itemMarginStart = itemMarginStart,
                    itemMarginEnd = itemMarginEnd,
                    itemTitleMarginStart = itemTitleMarginStart,
                    itemVerticalSpacerHeight = itemVerticalSpacerHeight,
                    itemVerticalSpacerPosition = itemVerticalSpacerPosition,
                ).let(TransformStyle.channelListStyleTransformer::transform)
            }
        }
    }
}
