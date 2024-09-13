
package network.ermis.ui.utils.extensions

import android.content.res.Resources
import android.util.DisplayMetrics
import kotlin.math.roundToInt

/**
 * Transforms DP value integer to pixels, based on the screen density.
 */
public fun Int.dpToPx(): Int = dpToPxPrecise().roundToInt()

/**
 * Uses the display metrics to transform the value of DP to pixels.
 */
public fun Int.dpToPxPrecise(): Float = (this * displayMetrics().density)

/**
 * Fetches the current system display metrics based on [Resources].
 */
internal fun displayMetrics(): DisplayMetrics = Resources.getSystem().displayMetrics
