package network.ermis.ui.utils.extension

import java.util.Date

/**
 * If the date is not older than one minute.
 */
public fun Date.isInLastMinute(): Boolean = (Date().time - ONE_MINUTE_IN_MILLIS < time)

private const val ONE_MINUTE_IN_MILLIS = 60000
