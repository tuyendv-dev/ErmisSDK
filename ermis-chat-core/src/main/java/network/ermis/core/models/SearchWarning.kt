package network.ermis.core.models

import androidx.compose.runtime.Immutable

/**
 * Represents a warning related to message search results. For example, if there are more
 * than 500 channels that match the channel filter.
 */
@Immutable
public data class SearchWarning(
    /**
     * Channel CIDs for the searched channels
     */
    val channelSearchCids: List<String>,

    /**
     * Number of channels searched
     */
    val channelSearchCount: Int,

    /**
     * Code corresponding to the warning
     */
    val warningCode: Int,

    /**
     * Description of the warning
     */
    val warningDescription: String,
)