
package network.ermis.ui.utils.extensions

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.annotation.StyleableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.res.getColorOrThrow
import androidx.core.content.res.getDimensionOrThrow
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Retrieves the Drawable for the attribute.
 *
 * Unlike [TypedArray.getDrawable], gracefully handles vector drawables with tints on API 21.
 *
 * @param context The context to inflate against.
 * @param id The index of attribute to retrieve.
 * @return An object that can be used to draw this resource.
 */
internal fun TypedArray.getDrawableCompat(context: Context, @StyleableRes id: Int): Drawable? {
    val resource = getResourceId(id, 0)
    if (resource != 0) {
        return AppCompatResources.getDrawable(context, resource)
    }
    return null
}

/**
 * Retrieves the ColorStateList for the attribute.
 *
 * @param context The context to inflate against.
 * @param id The index of attribute to retrieve.
 * @return An object that can be used to draw this resource.
 */
internal fun TypedArray.getColorStateListCompat(context: Context, @StyleableRes id: Int): ColorStateList? {
    val resource = getResourceId(id, 0)
    if (resource != 0) {
        return AppCompatResources.getColorStateList(context, resource)
    }
    return null
}

internal inline fun TypedArray.use(block: (TypedArray) -> Unit) {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    block(this)
    recycle()
}

internal inline fun <reified T : Enum<T>> TypedArray.getEnum(index: Int, default: T): T {
    return getInt(index, -1).let {
        if (it >= 0) enumValues<T>()[it] else default
    }
}

@ColorInt
internal fun TypedArray.getColorOrNull(@StyleableRes index: Int): Int? =
    runCatching { getColorOrThrow(index) }.getOrNull()

@Px
internal fun TypedArray.getDimensionOrNull(@StyleableRes index: Int): Float? =
    runCatching { getDimensionOrThrow(index) }.getOrNull()
