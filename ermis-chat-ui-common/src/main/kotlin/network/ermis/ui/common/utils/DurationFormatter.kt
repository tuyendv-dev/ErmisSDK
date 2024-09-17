package network.ermis.ui.common.utils

import kotlin.time.DurationUnit
import kotlin.time.toDuration

private const val TIME_DIVIDER = 60

public object DurationFormatter {

    /**
     * Formats duration in millis into string of mm:ss.
     */
    public fun formatDurationInMillis(durationInMillis: Int): String {
        val duration = durationInMillis.toDuration(DurationUnit.MILLISECONDS)
        val seconds = duration.inWholeSeconds.rem(TIME_DIVIDER).toString().padStart(2, '0')
        val minutes = duration.inWholeMinutes.rem(TIME_DIVIDER).toString().padStart(2, '0')

        return "$minutes:$seconds"
    }

    /**
     * Formats duration in seconds into string of mm:ss.
     */
    public fun formatDurationInSeconds(durationInSeconds: Float): String {
        val millis = durationInSeconds.times(1000).toInt()
        val duration = millis.toDuration(DurationUnit.MILLISECONDS)
        val seconds = duration.inWholeSeconds.rem(TIME_DIVIDER).toString().padStart(2, '0')
        val minutes = duration.inWholeMinutes.rem(TIME_DIVIDER).toString().padStart(2, '0')

        return "$minutes:$seconds"
    }
}
