package network.ermis.client.api.model.dto

import java.util.Date

/**
 * DTO to keep serialized date and also the original Date as Stirng as sent by backend.
 *
 * @param date The [Date] was parsed from backend of created locally.
 * @param rawDate The Date as a String. This is probably going to be the generated in the backend and can have
 * up to nanoseconds of precision.
 */
internal data class ExactDate(
    internal val date: Date,
    internal val rawDate: String,
)
