
package network.ermis.ui.widgets.avatar

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.annotation.Px
import network.ermis.ui.components.R
import network.ermis.ui.font.TextStyle
import network.ermis.ui.helper.TransformStyle
import network.ermis.ui.helper.ViewStyle
import network.ermis.ui.utils.extensions.dpToPx
import network.ermis.ui.utils.extensions.getColorCompat
import network.ermis.ui.utils.extensions.getDimension
import network.ermis.ui.utils.extensions.getEnum
import network.ermis.ui.utils.extensions.use

/**
 * Style for [UserAvatarView] and [ChannelAvatarView].
 */
public data class AvatarStyle(
    @Px public val avatarBorderWidth: Int,
    @ColorInt public val avatarBorderColor: Int,
    public val avatarInitialsTextStyle: TextStyle,
    public val groupAvatarInitialsTextStyle: TextStyle,
    public val onlineIndicatorEnabled: Boolean,
    public val onlineIndicatorPosition: OnlineIndicatorPosition,
    @ColorInt public val onlineIndicatorColor: Int,
    @ColorInt public val onlineIndicatorBorderColor: Int,
    public val avatarShape: AvatarShape,
    @Px public val borderRadius: Float,
) : ViewStyle {

    internal companion object {
        operator fun invoke(context: Context, attrs: AttributeSet?): AvatarStyle {
            context.obtainStyledAttributes(
                attrs,
                R.styleable.AvatarView,
                0,
                0,
            ).use {
                val avatarBorderWidth = it.getDimensionPixelSize(
                    R.styleable.AvatarView_ermisUiAvatarBorderWidth,
                    context.getDimension(R.dimen.ui_avatar_border_width),
                )

                val avatarBorderColor = it.getColor(
                    R.styleable.AvatarView_ermisUiAvatarBorderColor,
                    context.getColorCompat(R.color.ui_black),
                )

                val avatarInitialsTextStyle = TextStyle.Builder(it)
                    .size(
                        R.styleable.AvatarView_ermisUiAvatarTextSize,
                        context.getDimension(R.dimen.ui_avatar_initials),
                    )
                    .color(
                        R.styleable.AvatarView_ermisUiAvatarTextColor,
                        context.getColorCompat(R.color.ui_white),
                    )
                    .font(
                        R.styleable.AvatarView_ermisUiAvatarTextFontAssets,
                        R.styleable.AvatarView_ermisUiAvatarTextFont,
                    )
                    .style(
                        R.styleable.AvatarView_ermisUiAvatarTextStyle,
                        Typeface.BOLD,
                    )
                    .build()

                val groupAvatarInitialsTextStyle = TextStyle.Builder(it)
                    .size(
                        R.styleable.AvatarView_ermisUiGroupAvatarTextSize,
                        context.getDimension(R.dimen.ui_group_avatar_initials),
                    )
                    .color(
                        R.styleable.AvatarView_ermisUiGroupAvatarTextColor,
                        context.getColorCompat(R.color.ui_white),
                    )
                    .font(
                        R.styleable.AvatarView_ermisUiGroupAvatarTextFontAssets,
                        R.styleable.AvatarView_ermisUiGroupAvatarTextFont,
                    )
                    .style(
                        R.styleable.AvatarView_ermisUiGroupAvatarTextStyle,
                        Typeface.BOLD,
                    )
                    .build()

                val onlineIndicatorEnabled = it.getBoolean(
                    R.styleable.AvatarView_ermisUiAvatarOnlineIndicatorEnabled,
                    false,
                )

                val onlineIndicatorPosition = it.getEnum(
                    R.styleable.AvatarView_ermisUiAvatarOnlineIndicatorPosition,
                    OnlineIndicatorPosition.TOP_END,
                )
                val onlineIndicatorColor =
                    it.getColor(R.styleable.AvatarView_ermisUiAvatarOnlineIndicatorColor, Color.GREEN)

                val onlineIndicatorBorderColor =
                    it.getColor(
                        R.styleable.AvatarView_ermisUiAvatarOnlineIndicatorBorderColor,
                        context.getColorCompat(R.color.ui_white),
                    )

                val avatarShape =
                    it.getEnum(R.styleable.AvatarView_ermisUiAvatarShape, AvatarShape.CIRCLE)

                val borderRadius =
                    it.getDimensionPixelSize(
                        R.styleable.AvatarView_ermisUiAvatarBorderRadius,
                        4.dpToPx(),
                    ).toFloat()

                return AvatarStyle(
                    avatarBorderWidth = avatarBorderWidth,
                    avatarBorderColor = avatarBorderColor,
                    avatarInitialsTextStyle = avatarInitialsTextStyle,
                    groupAvatarInitialsTextStyle = groupAvatarInitialsTextStyle,
                    onlineIndicatorEnabled = onlineIndicatorEnabled,
                    onlineIndicatorPosition = onlineIndicatorPosition,
                    onlineIndicatorColor = onlineIndicatorColor,
                    onlineIndicatorBorderColor = onlineIndicatorBorderColor,
                    avatarShape = avatarShape,
                    borderRadius = borderRadius,
                ).let(TransformStyle.avatarStyleTransformer::transform)
            }
        }
    }
}
