
package network.ermis.ui.utils

import android.os.Build

/**
 * Checks whether the device manufacturer should consume long tap.
 * Fixes issue [#3255](https://github.com/GetStream/stream-chat-android/issues/3255)
 *
 * @return if manufacturer should consume long tap or not.
 */
internal fun shouldConsumeLongTap(): Boolean {
    val manufacturer = Build.MANUFACTURER.lowercase()
    return MANUFACTURERS_TO_CONSUME_LONG_TAP.any { it.lowercase() in manufacturer }
}

private const val MANUFACTURER_XIAOMI = "xiaomi"

/**
 * List of manufacturers which need to consume the long tap action.
 */
private val MANUFACTURERS_TO_CONSUME_LONG_TAP = listOf(
    MANUFACTURER_XIAOMI,
)
