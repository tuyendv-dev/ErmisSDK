package network.ermis.client.utils

import io.getstream.result.Result

/**
 * Converts [Result] into human-readable string.
 */
@JvmSynthetic
public inline fun <T : Any> Result<T>.stringify(toString: (data: T) -> String): String {
    return when (this) {
        is Result.Success -> toString(value)
        is Result.Failure -> value.toString()
    }
}
