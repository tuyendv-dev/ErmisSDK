@file:JvmName("DateUtils")

package network.ermis.core.utils

import network.ermis.core.models.TimeDuration
import java.util.Date
import kotlin.math.abs

/**
 * Tests if [this] date is after [that] date.
 */
public infix fun Date?.after(that: Date?): Boolean {
    return when {
        this == null -> false
        that == null -> true
        else -> this.after(that)
    }
}

/**
 * Returns the greater of two dates.
 */
public fun max(dateA: Date?, dateB: Date?): Date? = when (dateA after dateB) {
    true -> dateA
    else -> dateB
}

public fun min(dateA: Date?, dateB: Date?): Date? = when (dateA after dateB) {
    true -> dateB
    else -> dateA
}

public fun maxOf(vararg dates: Date?): Date? = dates.reduceOrNull { acc, date -> max(acc, date) }

public fun minOf(vararg dates: Date?): Date? = dates.reduceOrNull { acc, date -> min(acc, date) }

/**
 * Check if current date has difference with [other] no more that [offset].
 */
public fun Date.inOffsetWith(other: Date, offset: Long): Boolean = (time + offset) >= other.time

/**
 * Returns difference between [this] date and the [otherTime] in [TimeDuration].
 */
public fun Date.diff(otherTime: Long): TimeDuration {
    val diff = abs(time - otherTime)
    return TimeDuration.millis(diff)
}

/**
 * Returns difference between [this] date and [that] date in [TimeDuration].
 */
public fun Date.diff(that: Date): TimeDuration {
    val diff = abs(time - that.time)
    return TimeDuration.millis(diff)
}
