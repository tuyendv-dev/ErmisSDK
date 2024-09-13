
package network.ermis.ui.utils.extensions

import android.content.ContextWrapper
import android.content.res.ColorStateList
import android.graphics.drawable.RippleDrawable
import android.view.View
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.annotation.StringRes
import androidx.core.view.ViewCompat
import androidx.fragment.app.FragmentActivity
import network.ermis.ui.helper.ViewPadding

/**
 * Helper method for adding ripple effect to views
 *
 * @param color Color of the ripple.
 */
internal fun View.setBorderlessRipple(@ColorInt color: Int?) {
    background = if (color != null) {
        val rippleColor = ColorStateList.valueOf(color)
        RippleDrawable(rippleColor, null, background)
    } else {
        null
    }
}

/**
 * Ensures the context being accessed in a View can be cast to Activity.
 */
public val View.activity: FragmentActivity?
    get() {
        var context = context
        while (context is ContextWrapper) {
            if (context is FragmentActivity) {
                return context
            }
            context = context.baseContext
        }
        return null
    }

public fun View.showToast(@StringRes resId: Int) {
    Toast.makeText(context, context.getString(resId), Toast.LENGTH_SHORT).show()
}

public fun View.showToast(mess: String) {
    Toast.makeText(context, mess, Toast.LENGTH_SHORT).show()
}

internal fun View.setPaddingStart(@Px start: Int) {
    val isRtl = context.isRtlLayout

    if (isRtl) {
        setPadding(paddingLeft, paddingTop, start, paddingBottom)
    } else {
        setPadding(start, paddingTop, paddingRight, paddingBottom)
    }
}

internal fun View.setPaddingCompat(padding: ViewPadding) {
    ViewCompat.setPaddingRelative(
        this,
        padding.start,
        padding.top,
        padding.end,
        padding.bottom,
    )
}
