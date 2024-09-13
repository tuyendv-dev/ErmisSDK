
package network.ermis.ui.widgets.typing

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.annotation.LayoutRes
import network.ermis.ui.components.R
import network.ermis.ui.font.TextStyle
import network.ermis.ui.helper.TransformStyle
import network.ermis.ui.helper.ViewStyle
import network.ermis.ui.utils.extensions.getColorCompat
import network.ermis.ui.utils.extensions.getDimension
import network.ermis.ui.utils.extensions.use

/**
 * Style for [TypingIndicatorView].
 * Use this class together with [TransformStyle.typingIndicatorViewStyleTransformer] to change [TypingIndicatorView] style programmatically.
 *
 * @property typingIndicatorAnimationView Typing animation view. Default value is [R.layout.typing_indicator_animation_view].
 * @property typingIndicatorUsersTextStyle Appearance for typing users text.
 */
public data class TypingIndicatorViewStyle(
    @LayoutRes public val typingIndicatorAnimationView: Int,
    public val typingIndicatorUsersTextStyle: TextStyle,
) : ViewStyle {
    internal companion object {
        operator fun invoke(context: Context, attrs: AttributeSet?): TypingIndicatorViewStyle {
            context.obtainStyledAttributes(
                attrs,
                R.styleable.TypingIndicatorView,
                R.attr.ermisUiTypingIndicatorView,
                R.style.ermisUi_TypingIndicatorView,
            ).use { a ->
                val typingIndicatorAnimationView = a.getResourceId(
                    R.styleable.TypingIndicatorView_ermisUiTypingIndicatorAnimationView,
                    R.layout.typing_indicator_animation_view,
                )

                val typingIndicatorUsersTextStyle = TextStyle.Builder(a)
                    .size(
                        R.styleable.TypingIndicatorView_ermisUiTypingIndicatorUsersTextSize,
                        context.getDimension(R.dimen.ui_text_small),
                    )
                    .color(
                        R.styleable.TypingIndicatorView_ermisUiTypingIndicatorUsersTextColor,
                        context.getColorCompat(R.color.ui_text_color_secondary),
                    )
                    .font(
                        R.styleable.TypingIndicatorView_ermisUiTypingIndicatorUsersTextFontAssets,
                        R.styleable.TypingIndicatorView_ermisUiTypingIndicatorUsersTextFont,
                    )
                    .style(
                        R.styleable.TypingIndicatorView_ermisUiTypingIndicatorUsersTextStyle,
                        Typeface.NORMAL,
                    )
                    .build()

                return TypingIndicatorViewStyle(
                    typingIndicatorAnimationView = typingIndicatorAnimationView,
                    typingIndicatorUsersTextStyle = typingIndicatorUsersTextStyle,
                ).let(TransformStyle.typingIndicatorViewStyleTransformer::transform)
            }
        }
    }
}
