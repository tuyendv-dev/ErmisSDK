package network.ermis.client.utils.extensions.internal

import java.util.Date

/**
 * Checks if the Date is later than [daysInMillis].
 */
internal fun Date.isLaterThanDays(daysInMillis: Long): Boolean {
    val now = Date()
    return now.time - time > daysInMillis
}
