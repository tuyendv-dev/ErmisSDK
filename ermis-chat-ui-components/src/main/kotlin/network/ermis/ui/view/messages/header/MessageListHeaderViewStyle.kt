
package network.ermis.ui.view.messages.header

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import network.ermis.ui.components.R
import network.ermis.ui.font.TextStyle
import network.ermis.ui.helper.TransformStyle
import network.ermis.ui.helper.ViewStyle
import network.ermis.ui.utils.extensions.getColorCompat
import network.ermis.ui.utils.extensions.getDimension
import network.ermis.ui.utils.extensions.getDrawableCompat
import network.ermis.ui.utils.extensions.use

/**
 * Style for [MessageListHeaderView].
 * Use this class together with [TransformStyle.messageListHeaderStyleTransformer] to change [MessageListHeaderView] styles programmatically.
 *
 * @property titleTextStyle Appearance for title text.
 * @property offlineTextStyle Appearance for offline text.
 * @property searchingForNetworkTextStyle Appearance for searching for network text.
 * @property onlineTextStyle Appearance for online text.
 * @property showUserAvatar Shows/hides user avatar. Shown by default.
 * @property backButtonIcon Icon for back button. Default value is [R.drawable.arrow_left].
 * @property showBackButton Shows/hides back button. Shown by default.
 * @property showBackButtonBadge Shows/hides unread badge. Hidden by default.
 * @property backButtonBadgeBackgroundColor Unread badge color. Default value is [R.color.ui_accent_red].
 * @property showSearchingForNetworkProgressBar Shows/hides searching for network progress bar. Shown by default.
 * @property searchingForNetworkProgressBarTint Progress bar tint color. Default value is [R.color.ui_accent_blue].
 * @property separatorBackgroundDrawable Background drawable of the separator at the bottom of [MessageListHeaderView].
 */
public data class MessageListHeaderViewStyle(
    @ColorInt public val background: Int,
    public val titleTextStyle: TextStyle,
    public val offlineTextStyle: TextStyle,
    public val searchingForNetworkTextStyle: TextStyle,
    public val onlineTextStyle: TextStyle,
    public val showUserAvatar: Boolean,
    public val backButtonIcon: Drawable,
    public val showBackButton: Boolean,
    public val showBackButtonBadge: Boolean,
    @ColorInt public val backButtonBadgeBackgroundColor: Int,
    public val showSearchingForNetworkProgressBar: Boolean,
    public val searchingForNetworkProgressBarTint: ColorStateList,
    public val separatorBackgroundDrawable: Drawable?,
) : ViewStyle {

    internal companion object {
        operator fun invoke(context: Context, attrs: AttributeSet?): MessageListHeaderViewStyle {
            context.obtainStyledAttributes(
                attrs,
                R.styleable.MessageListHeaderView,
                R.attr.ermisUiMessageListHeaderStyle,
                R.style.ermisUi_MessageListHeader,
            ).use { a ->
                val background = a.getColor(
                    R.styleable.MessageListHeaderView_ermisUiMessageListHeaderBackground,
                    context.getColorCompat(R.color.ui_white),
                )

                val showUserAvatar =
                    a.getBoolean(R.styleable.MessageListHeaderView_ermisUiMessageListHeaderShowUserAvatar, true)

                val backButtonIcon =
                    a.getDrawable(R.styleable.MessageListHeaderView_ermisUiMessageListHeaderBackButtonIcon)
                        ?: context.getDrawableCompat(R.drawable.arrow_left)!!

                val titleTextStyle = TextStyle.Builder(a)
                    .size(
                        R.styleable.MessageListHeaderView_ermisUiMessageListHeaderTitleTextSize,
                        context.getDimension(R.dimen.ui_text_large),
                    )
                    .color(
                        R.styleable.MessageListHeaderView_ermisUiMessageListHeaderTitleTextColor,
                        context.getColorCompat(R.color.ui_text_color_primary),
                    )
                    .font(
                        R.styleable.MessageListHeaderView_ermisUiMessageListHeaderTitleFontAssets,
                        R.styleable.MessageListHeaderView_ermisUiMessageListHeaderTitleTextFont,
                    )
                    .style(
                        R.styleable.MessageListHeaderView_ermisUiMessageListHeaderTitleTextStyle,
                        Typeface.BOLD,
                    ).build()

                val showBackButton =
                    a.getBoolean(R.styleable.MessageListHeaderView_ermisUiMessageListHeaderShowBackButton, true)

                val showBackButtonBadge =
                    a.getBoolean(R.styleable.MessageListHeaderView_ermisUiMessageListHeaderShowBackButtonBadge, false)

                val backButtonBadgeBackgroundColor = a.getColor(
                    R.styleable.MessageListHeaderView_ermisUiMessageListHeaderBackButtonBadgeBackgroundColor,
                    context.getColorCompat(R.color.ui_accent_red),
                )

                val offlineTextStyle = TextStyle.Builder(a)
                    .size(
                        R.styleable.MessageListHeaderView_ermisUiMessageListHeaderOfflineLabelTextSize,
                        context.getDimension(R.dimen.ui_text_small),
                    )
                    .color(
                        R.styleable.MessageListHeaderView_ermisUiMessageListHeaderOfflineLabelTextColor,
                        context.getColorCompat(R.color.ui_text_color_secondary),
                    )
                    .font(
                        R.styleable.MessageListHeaderView_ermisUiMessageListHeaderOfflineLabelFontAssets,
                        R.styleable.MessageListHeaderView_ermisUiMessageListHeaderOfflineLabelTextFont,
                    )
                    .style(
                        R.styleable.MessageListHeaderView_ermisUiMessageListHeaderOfflineLabelTextStyle,
                        Typeface.NORMAL,
                    )
                    .build()

                val searchingForNetworkTextStyle = TextStyle.Builder(a)
                    .size(
                        R.styleable.MessageListHeaderView_ermisUiMessageListHeaderSearchingForNetworkLabelTextSize,
                        context.getDimension(R.dimen.ui_text_small),
                    )
                    .color(
                        R.styleable.MessageListHeaderView_ermisUiMessageListHeaderSearchingForNetworkLabelColor,
                        context.getColorCompat(R.color.ui_text_color_secondary),
                    )
                    .font(
                        R.styleable.MessageListHeaderView_ermisUiMessageListHeaderSearchingForNetworkLabelFontAssets,
                        R.styleable.MessageListHeaderView_ermisUiMessageListHeaderSearchingForNetworkLabelTextFont,
                    )
                    .style(
                        R.styleable.MessageListHeaderView_ermisUiMessageListHeaderSearchingForNetworkLabelTextStyle,
                        Typeface.NORMAL,
                    )
                    .build()

                val showSearchingForNetworkProgressBar = a.getBoolean(
                    R.styleable.MessageListHeaderView_ermisUiMessageListHeaderShowSearchingForNetworkProgressBar,
                    true,
                )

                val searchingForNetworkProgressBarTint = a.getColorStateList(
                    R.styleable.MessageListHeaderView_ermisUiMessageListHeaderSearchingForNetworkProgressBarTint,
                ) ?: ContextCompat.getColorStateList(context, R.color.ui_accent_blue)!!

                val onlineTextStyle = TextStyle.Builder(a)
                    .size(
                        R.styleable.MessageListHeaderView_ermisUiMessageListHeaderDefaultLabelTextSize,
                        context.getDimension(R.dimen.ui_text_small),
                    )
                    .color(
                        R.styleable.MessageListHeaderView_ermisUiMessageListHeaderDefaultLabelTextColor,
                        context.getColorCompat(R.color.ui_text_color_secondary),
                    )
                    .font(
                        R.styleable.MessageListHeaderView_ermisUiMessageListHeaderDefaultLabelFontAssets,
                        R.styleable.MessageListHeaderView_ermisUiMessageListHeaderDefaultLabelTextFont,
                    )
                    .style(
                        R.styleable.MessageListHeaderView_ermisUiMessageListHeaderDefaultLabelTextStyle,
                        Typeface.NORMAL,
                    )
                    .build()

                val separatorBackgroundDrawable =
                    a.getDrawable(
                        R.styleable.MessageListHeaderView_ermisUiMessageListHeaderSeparatorBackgroundDrawable,
                    )

                return MessageListHeaderViewStyle(
                    background = background,
                    titleTextStyle = titleTextStyle,
                    offlineTextStyle = offlineTextStyle,
                    searchingForNetworkTextStyle = searchingForNetworkTextStyle,
                    onlineTextStyle = onlineTextStyle,
                    showUserAvatar = showUserAvatar,
                    backButtonIcon = backButtonIcon,
                    showBackButton = showBackButton,
                    showBackButtonBadge = showBackButtonBadge,
                    backButtonBadgeBackgroundColor = backButtonBadgeBackgroundColor,
                    showSearchingForNetworkProgressBar = showSearchingForNetworkProgressBar,
                    searchingForNetworkProgressBarTint = searchingForNetworkProgressBarTint,
                    separatorBackgroundDrawable = separatorBackgroundDrawable,
                ).let(TransformStyle.messageListHeaderStyleTransformer::transform)
            }
        }
    }
}
