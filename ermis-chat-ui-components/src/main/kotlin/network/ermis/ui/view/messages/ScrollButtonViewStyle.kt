
package network.ermis.ui.view.messages

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.StyleableRes
import network.ermis.ui.components.R
import network.ermis.ui.view.messages.internal.ScrollButtonView
import network.ermis.ui.font.TextStyle
import network.ermis.ui.helper.TransformStyle
import network.ermis.ui.helper.ViewStyle
import network.ermis.ui.utils.extensions.getColorCompat
import network.ermis.ui.utils.extensions.getColorOrNull
import network.ermis.ui.utils.extensions.getDimension

/**
 * Style for [ScrollButtonView].
 *
 * @property scrollButtonEnabled Enables/disables view which allows to scroll to the latest messages. Default value is true.
 * @property scrollButtonUnreadEnabled Enables/disables unread label. Default value is enabled.
 * @property scrollButtonColor [ScrollButtonView] background color. Default value is [R.color.ui_white].
 * @property scrollButtonRippleColor [ScrollButtonView] ripple color. Default value is [R.color.ui_white_smoke].
 * @property scrollButtonBadgeColor Unread count label background color. Default value is [R.color.ui_accent_blue].
 * @property scrollButtonIcon [ScrollButtonView] icon. Default value is [R.drawable.ic_down].
 * @property scrollButtonBadgeTextStyle Text appearance of the unread count label.
 * @property scrollButtonBadgeIcon The icon used for the unread count label. Default value is [R.drawable.shape_scroll_button_badge].
 * @property scrollButtonBadgeGravity The gravity of the unread count label. Default value is center top.
 * @property scrollButtonBadgeElevation The elevation used for the unread count label.
 * Default value is [MessageListViewStyle.DEFAULT_SCROLL_BUTTON_BADGE_ELEVATION].
 * @property scrollButtonInternalMargin The internal margins for the scroll button.
 * This affects the distance between the scroll button and the unread count label.
 */
public data class ScrollButtonViewStyle(
    public val scrollButtonEnabled: Boolean,
    public val scrollButtonUnreadEnabled: Boolean,
    @ColorInt public val scrollButtonColor: Int,
    @ColorInt public val scrollButtonRippleColor: Int,
    @ColorInt public val scrollButtonBadgeColor: Int?,
    public val scrollButtonElevation: Float,
    public val scrollButtonIcon: Drawable?,
    public val scrollButtonBadgeTextStyle: TextStyle,
    public val scrollButtonBadgeIcon: Drawable?,
    public val scrollButtonBadgeGravity: Int,
    public val scrollButtonBadgeElevation: Float,
    public val scrollButtonInternalMargin: Int,
) : ViewStyle {

    internal class Builder(private val context: Context, private val attrs: TypedArray) {
        private var scrollButtonEnabled: Boolean = false
        private var scrollButtonUnreadEnabled: Boolean = false

        @ColorInt
        private var scrollButtonColor: Int = 0

        @ColorInt
        private var scrollButtonRippleColor: Int = 0

        @ColorInt
        private var scrollButtonBadgeColor: Int? = 0
        private var scrollButtonIcon: Drawable? = null
        private var scrollButtonElevation: Float = 0F
        private var scrollButtonBadgeGravity: Int = 0
        private var scrollButtonBadgeIcon: Drawable? = null
        private var scrollButtonBadgeElevation: Float = 0f
        private var scrollButtonInternalMargin: Int = 0

        fun scrollButtonEnabled(
            @StyleableRes scrollButtonEnabledStyleableId: Int,
            defaultValue: Boolean,
        ) = apply {
            scrollButtonEnabled = attrs.getBoolean(scrollButtonEnabledStyleableId, defaultValue)
        }

        fun scrollButtonUnreadEnabled(
            @StyleableRes scrollButtonUnreadEnabledStyleableId: Int,
            defaultValue: Boolean,
        ) = apply {
            scrollButtonUnreadEnabled = attrs.getBoolean(scrollButtonUnreadEnabledStyleableId, defaultValue)
        }

        fun scrollButtonColor(
            @StyleableRes scrollButtonColorStyleableId: Int,
            @ColorInt defaultValue: Int,
        ) = apply {
            scrollButtonColor = attrs.getColor(scrollButtonColorStyleableId, defaultValue)
        }

        fun scrollButtonRippleColor(
            @StyleableRes scrollButtonRippleColorStyleableId: Int,
            @ColorInt defaultColor: Int,
        ) = apply {
            scrollButtonRippleColor = attrs.getColor(scrollButtonRippleColorStyleableId, defaultColor)
        }

        fun scrollButtonBadgeColor(
            @StyleableRes scrollButtonBadgeColorStyleableId: Int,
        ) = apply {
            scrollButtonBadgeColor = attrs.getColorOrNull(scrollButtonBadgeColorStyleableId)
        }

        fun scrollButtonIcon(
            @StyleableRes scrollButtonIconStyleableId: Int,
            defaultIcon: Drawable?,
        ) = apply {
            scrollButtonIcon = attrs.getDrawable(scrollButtonIconStyleableId) ?: defaultIcon
        }

        fun scrollButtonElevation(
            @StyleableRes scrollButtonElevation: Int,
            defaultElevation: Float,
        ) = apply {
            this.scrollButtonElevation = attrs.getDimension(scrollButtonElevation, defaultElevation)
        }

        fun scrollButtonBadgeGravity(
            @StyleableRes scrollButtonBadgeGravity: Int,
            defaultGravity: Int,
        ): Builder = apply {
            this.scrollButtonBadgeGravity = attrs.getInt(scrollButtonBadgeGravity, defaultGravity)
        }

        fun scrollButtonBadgeIcon(
            @StyleableRes scrollButtonBadgeIcon: Int,
            defaultIcon: Drawable?,
        ) = apply {
            this.scrollButtonBadgeIcon = attrs.getDrawable(scrollButtonBadgeIcon) ?: defaultIcon
        }

        fun scrollButtonBadgeElevation(
            @StyleableRes scrollButtonBadgeElevation: Int,
            defaultElevation: Float,
        ) = apply {
            this.scrollButtonBadgeElevation = attrs.getDimension(scrollButtonBadgeElevation, defaultElevation)
        }

        fun scrollButtonBadgeInternalMargin(
            @StyleableRes scrollButtonInternalMargin: Int,
            defaultMargin: Int,
        ) = apply {
            this.scrollButtonInternalMargin = attrs.getDimensionPixelSize(scrollButtonInternalMargin, defaultMargin)
        }

        fun build(): ScrollButtonViewStyle {
            val scrollButtonBadgeTextStyle = TextStyle.Builder(attrs)
                .size(
                    R.styleable.MessageListView_ermisUiScrollButtonBadgeTextSize,
                    context.getDimension(R.dimen.ermis_ui_scroll_button_unread_badge_text_size),
                )
                .color(
                    R.styleable.MessageListView_ermisUiScrollButtonBadgeTextColor,
                    context.getColorCompat(R.color.ui_literal_white),
                )
                .font(
                    R.styleable.MessageListView_ermisUiScrollButtonBadgeFontAssets,
                    R.styleable.MessageListView_ermisUiScrollButtonBadgeTextFont,
                )
                .style(R.styleable.MessageListView_ermisUiScrollButtonBadgeTextStyle, Typeface.BOLD)
                .build()

            return ScrollButtonViewStyle(
                scrollButtonEnabled = scrollButtonEnabled,
                scrollButtonUnreadEnabled = scrollButtonUnreadEnabled,
                scrollButtonColor = scrollButtonColor,
                scrollButtonRippleColor = scrollButtonRippleColor,
                scrollButtonBadgeColor = scrollButtonBadgeColor,
                scrollButtonIcon = scrollButtonIcon,
                scrollButtonBadgeTextStyle = scrollButtonBadgeTextStyle,
                scrollButtonElevation = scrollButtonElevation,
                scrollButtonBadgeIcon = scrollButtonBadgeIcon,
                scrollButtonBadgeGravity = scrollButtonBadgeGravity,
                scrollButtonBadgeElevation = scrollButtonBadgeElevation,
                scrollButtonInternalMargin = scrollButtonInternalMargin,
            ).let(TransformStyle.scrollButtonStyleTransformer::transform)
        }
    }
}
