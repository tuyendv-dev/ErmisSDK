
package network.ermis.ui.utils

import android.content.res.ColorStateList
import androidx.annotation.ColorInt

internal fun getColorList(
    @ColorInt normalColor: Int,
    @ColorInt selectedColor: Int,
    @ColorInt disabledColor: Int,
) = ColorStateList(
    arrayOf(
        intArrayOf(android.R.attr.state_enabled, -android.R.attr.state_selected),
        intArrayOf(android.R.attr.state_enabled, android.R.attr.state_selected),
        intArrayOf(-android.R.attr.state_enabled),
    ),
    intArrayOf(normalColor, selectedColor, disabledColor),
)
