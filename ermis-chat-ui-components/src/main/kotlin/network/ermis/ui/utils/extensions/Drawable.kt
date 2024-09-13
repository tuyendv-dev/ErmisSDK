
package network.ermis.ui.utils.extensions

import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.core.graphics.drawable.DrawableCompat

/**
 * Applies a tint for a drawable. Returns the original Drawable if the input color
 * is null.
 *
 * @param tintColor The color used for tinting this Drawable.
 */
internal fun Drawable.applyTint(@ColorInt tintColor: Int?): Drawable {
    if (tintColor == null) return this

    val tintedDrawable = DrawableCompat.wrap(this).mutate()
    DrawableCompat.setTint(tintedDrawable, tintColor)
    DrawableCompat.setTintMode(tintedDrawable, PorterDuff.Mode.SRC_IN)
    return tintedDrawable
}
