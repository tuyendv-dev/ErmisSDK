
package network.ermis.ui.view.search

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.annotation.Px
import network.ermis.ui.components.R
import network.ermis.ui.helper.TransformStyle
import network.ermis.ui.helper.ViewStyle
import network.ermis.ui.utils.extensions.getColorCompat
import network.ermis.ui.utils.extensions.getColorOrNull
import network.ermis.ui.utils.extensions.getDimension
import network.ermis.ui.utils.extensions.getDimensionOrNull
import network.ermis.ui.utils.extensions.getDrawableCompat
import network.ermis.ui.utils.extensions.use
import kotlin.math.roundToInt

/**
 * @property textColor Color value of the search input text.
 * @property hintColor Color value of the search input hint.
 * @property searchIconDrawable Drawable of search icon visible on the right side of the SearchInputView.
 * @property clearInputDrawable Drawable of clear input icon visible on the left side of the SearchInputView.
 * @property backgroundDrawable Drawable used as the view's background.
 * @property containerBackgroundColor Color of the container background.
 * @property hintText Hint text.
 * @property textSize The size of the text in the input.
 * @property searchInputHeight The height of the root container.
 * @property searchIconWidth The width of the search icon.
 * @property searchIconHeight The height of the search icon.
 * @property searchIconMarginStart The start margin of the search icon.
 * @property clearIconWidth The width of the clear icon.
 * @property clearIconHeight The height of the clear icon.
 * @property clearIconMarginEnd The end margin of the clear icon.
 * @property textMarginStart The start margin of the input text.
 * @property textMarginEnd The end margin of the input text.
 */
public data class SearchInputViewStyle(
    @ColorInt val textColor: Int,
    @ColorInt val hintColor: Int,
    val searchIconDrawable: Drawable,
    val clearInputDrawable: Drawable,
    val backgroundDrawable: Drawable,
    val backgroundDrawableOutline: DrawableOutline?,
    @ColorInt val containerBackgroundColor: Int,
    val hintText: String,
    val textSize: Int,
    @Px val textMarginStart: Int,
    @Px val textMarginEnd: Int,
    val searchInputHeight: Int,
    @Px val searchIconWidth: Int,
    @Px val searchIconHeight: Int,
    @Px val searchIconMarginStart: Int,
    @Px val clearIconWidth: Int,
    @Px val clearIconHeight: Int,
    @Px val clearIconMarginEnd: Int,
) : ViewStyle {
    internal companion object {
        operator fun invoke(context: Context, attrs: AttributeSet?): SearchInputViewStyle {
            context.obtainStyledAttributes(
                attrs,
                R.styleable.SearchInputView,
                R.attr.ermisUiSearchInputViewStyle,
                R.style.ermisUi_SearchInputView,
            ).use { a ->
                val searchIcon = a.getDrawable(R.styleable.SearchInputView_ermisUiSearchInputViewSearchIcon)
                    ?: context.getDrawableCompat(R.drawable.ic_search)!!

                val clearIcon = a.getDrawable(R.styleable.SearchInputView_ermisUiSearchInputViewClearInputIcon)
                    ?: context.getDrawableCompat(R.drawable.ic_clear)!!

                val backgroundDrawable = a.getDrawable(R.styleable.SearchInputView_ermisUiSearchInputViewBackground)
                    ?: context.getDrawableCompat(R.drawable.shape_search_view_background)!!

                var backgroundDrawableOutline: DrawableOutline? = null

                val backgroundDrawableOutlineColor = a.getColorOrNull(
                    R.styleable.SearchInputView_ermisUiSearchInputViewBackgroundOutlineColor,
                )
                val backgroundDrawableOutlineWidth = a.getDimensionOrNull(
                    R.styleable.SearchInputView_ermisUiSearchInputViewBackgroundOutlineWidth,
                )?.roundToInt()
                if (backgroundDrawableOutlineColor != null && backgroundDrawableOutlineWidth != null) {
                    backgroundDrawableOutline = DrawableOutline(
                        width = backgroundDrawableOutlineWidth,
                        color = backgroundDrawableOutlineColor,
                    )
                }

                val containerBackground = a.getColor(
                    R.styleable.SearchInputView_ermisUiSearchInputViewContainerBackground,
                    context.getColorCompat(R.color.ui_white),
                )

                val textColor = a.getColor(
                    R.styleable.SearchInputView_ermisUiSearchInputViewTextColor,
                    context.getColorCompat(R.color.ui_text_color_primary),
                )

                val hintColor = a.getColor(
                    R.styleable.SearchInputView_ermisUiSearchInputViewHintColor,
                    context.getColorCompat(R.color.ui_text_color_primary),
                )

                val hintText = a.getText(R.styleable.SearchInputView_ermisUiSearchInputViewHintText)?.toString()
                    ?: context.getString(R.string.ermis_ui_search_input_hint)

                val textSize = a.getDimensionPixelSize(
                    R.styleable.SearchInputView_ermisUiSearchInputViewTextSize,
                    context.getDimension(R.dimen.ui_text_medium),
                )

                val textMarginStart = a.getDimensionPixelSize(
                    R.styleable.SearchInputView_ermisUiSearchInputViewTextMarginStart,
                    context.getDimension(R.dimen.ui_search_input_text_margin_start),
                )

                val textMarginEnd = a.getDimensionPixelSize(
                    R.styleable.SearchInputView_ermisUiSearchInputViewTextMarginEnd,
                    context.getDimension(R.dimen.ui_search_input_text_margin_end),
                )

                val searchInputHeight = a.getDimensionPixelSize(
                    R.styleable.SearchInputView_ermisUiSearchInputViewHeight,
                    context.getDimension(R.dimen.ui_search_input_height),
                )

                val searchIconWidth = a.getDimensionPixelSize(
                    R.styleable.SearchInputView_ermisUiSearchInputViewIconSearchWidth,
                    context.getDimension(R.dimen.ui_search_input_icon_search_width),
                )

                val searchIconHeight = a.getDimensionPixelSize(
                    R.styleable.SearchInputView_ermisUiSearchInputViewIconSearchHeight,
                    context.getDimension(R.dimen.ui_search_input_icon_search_height),
                )

                val searchIconMarginStart = a.getDimensionPixelSize(
                    R.styleable.SearchInputView_ermisUiSearchInputViewIconSearchMarginStart,
                    context.getDimension(R.dimen.ui_search_input_icon_search_margin_start),
                )

                val clearIconWidth = a.getDimensionPixelSize(
                    R.styleable.SearchInputView_ermisUiSearchInputViewIconClearWidth,
                    context.getDimension(R.dimen.ui_search_input_icon_clear_width),
                )

                val clearIconHeight = a.getDimensionPixelSize(
                    R.styleable.SearchInputView_ermisUiSearchInputViewIconClearHeight,
                    context.getDimension(R.dimen.ui_search_input_icon_clear_height),
                )

                val clearIconMarginEnd = a.getDimensionPixelSize(
                    R.styleable.SearchInputView_ermisUiSearchInputViewIconClearMarginEnd,
                    context.getDimension(R.dimen.ui_search_input_icon_clear_margin_end),
                )

                return SearchInputViewStyle(
                    searchIconDrawable = searchIcon,
                    clearInputDrawable = clearIcon,
                    backgroundDrawable = backgroundDrawable,
                    backgroundDrawableOutline = backgroundDrawableOutline,
                    containerBackgroundColor = containerBackground,
                    textColor = textColor,
                    hintColor = hintColor,
                    hintText = hintText,
                    textSize = textSize,
                    textMarginStart = textMarginStart,
                    textMarginEnd = textMarginEnd,
                    searchInputHeight = searchInputHeight,
                    searchIconWidth = searchIconWidth,
                    searchIconHeight = searchIconHeight,
                    searchIconMarginStart = searchIconMarginStart,
                    clearIconWidth = clearIconWidth,
                    clearIconHeight = clearIconHeight,
                    clearIconMarginEnd = clearIconMarginEnd,
                ).let(TransformStyle.searchInputViewStyleTransformer::transform)
            }
        }
    }

    /**
     * Represents the outline of the drawable.
     *
     * @property color Color of the drawable outline.
     * @property width Width of the drawable outline.
     */
    public data class DrawableOutline(
        @Px val width: Int,
        @ColorInt val color: Int,
    )
}
