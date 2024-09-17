package network.ermis.ui.common.feature.messages.query.filter

import io.getstream.log.taggedLogger
import network.ermis.ui.common.feature.messages.composer.transliteration.DefaultStreamTransliterator
import network.ermis.ui.common.feature.messages.composer.transliteration.StreamTransliterator
import network.ermis.ui.common.feature.messages.query.formatter.Combine
import network.ermis.ui.common.feature.messages.query.formatter.IgnoreDiacritics
import network.ermis.ui.common.feature.messages.query.formatter.Lowercase
import network.ermis.ui.common.feature.messages.query.formatter.QueryFormatter
import network.ermis.ui.common.feature.messages.query.formatter.Transliterate
import kotlin.math.min

/**
 * Default implementation of [QueryFilter].
 *
 * This implementation of [QueryFilter] ignores upper case, diacritics
 * It uses levenshtein approximation so typos are included in the search.
 *
 * It is possible to choose a transliteration by providing a [transliterator].
 *
 * @param transliterator The transliterator to use for transliterating the query string.
 * @param target The function to extract the target string from the item.
 */
public class DefaultQueryFilter<T>(
    private val transliterator: StreamTransliterator = DefaultStreamTransliterator(),
    private val target: (T) -> String,
) : QueryFilter<T> {

    private val logger by taggedLogger("Chat:InputQueryFilter")

    private val queryFormatter: QueryFormatter = Combine(
        Lowercase(),
        IgnoreDiacritics(),
        Transliterate(transliterator),
    )

    override fun filter(items: List<T>, query: String): List<T> {
        logger.d { "[filter] query: \"$query\", items.size: ${items.size}" }
        val formattedQuery = queryFormatter.format(query)
        return items.asSequence()
            .map { it.measureDistance(formattedQuery) }
            .filter { it.distance < MAX_DISTANCE }
            .sorted()
            .onEach { logger.v { "[filter] target: \"${target(it.item)}\", distance: ${it.distance}" } }
            .map { it.item }
            .toList()
    }

    private fun T.measureDistance(formattedQuery: String): MeasuredItem<T> {
        val target = target(this)
        if (target.isEmpty() || formattedQuery.length > target.length) {
            logger.v { "[measureDistance] #skip; target: \"$target\", formattedQuery: \"$formattedQuery\"" }
            return MeasuredItem(this, Int.MAX_VALUE)
        }
        val formattedTarget = queryFormatter.format(target)
        val distance = when (formattedTarget.contains(formattedQuery, ignoreCase = true)) {
            true -> 0
            else -> {
                val finalTarget = when (formattedTarget.length > formattedQuery.length) {
                    true -> formattedTarget.substring(0, formattedQuery.length)
                    else -> formattedTarget
                }
                levenshteinDistance(formattedQuery, finalTarget)
            }
        }
        return MeasuredItem(this, distance)
    }

    private data class MeasuredItem<T>(val item: T, val distance: Int) : Comparable<MeasuredItem<T>> {
        override fun compareTo(other: MeasuredItem<T>): Int {
            return distance.compareTo(other.distance)
        }
    }

    private fun minLevenshteinDistance(search: String, target: String): Int {
        val totalDistance = levenshteinDistance(search, target)
        val wordDistance = wordLevenshteinDistance(search, target)
        return minOf(totalDistance, wordDistance)
    }

    private fun wordLevenshteinDistance(search: String, target: String): Int {
        try {
            if (search.isEmpty() || target.isEmpty()) {
                return Int.MAX_VALUE
            }
            var distance = Int.MAX_VALUE
            var sStartIndex = 0
            var tStartIndex = 0
            while (true) {
                val sEndIndex = search.indexOf(startIndex = sStartIndex, char = SPACE)
                val tEndIndex = target.indexOf(startIndex = tStartIndex, char = SPACE)
                if (tEndIndex == -1) {
                    break
                }
                val subSearch = if (sEndIndex == -1) search else search.substring(sStartIndex, sEndIndex)
                val subTarget = target.substring(tStartIndex, tEndIndex)
                val subDistance = levenshteinDistance(subSearch, subTarget)
                distance = minOf(distance, subDistance)
                sStartIndex = sEndIndex + 1
                tStartIndex = tEndIndex + 1
            }
            return distance
        } catch (e: Throwable) {
            logger.e(e) { "[wordLevenshteinDistance] failed: $e" }
            return Int.MAX_VALUE
        }
    }

    private fun levenshteinDistance(search: String, target: String): Int {
        when {
            search == target -> return 0
            search.isEmpty() -> return target.length
            target.isEmpty() -> return search.length
        }

        val searchLength = search.length + 1
        val targetLength = target.length + 1

        var cost = Array(searchLength) { it }
        var newCost = Array(searchLength) { 0 }

        for (i in 1 until targetLength) {
            newCost[0] = i

            for (j in 1 until searchLength) {
                val match = if (search[j - 1] == target[i - 1]) 0 else 1

                val costReplace = cost[j - 1] + match
                val costInsert = cost[j] + 1
                val costDelete = newCost[j - 1] + 1

                newCost[j] = min(min(costInsert, costDelete), costReplace)
            }

            val swap = cost
            cost = newCost
            newCost = swap
        }

        return cost[searchLength - 1]
    }

    private companion object {
        private const val MAX_DISTANCE = 3
        private const val SPACE = ' '
    }
}
