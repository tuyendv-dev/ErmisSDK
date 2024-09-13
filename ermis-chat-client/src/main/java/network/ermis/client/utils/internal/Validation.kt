package network.ermis.client.utils.internal

import io.getstream.result.Error
import io.getstream.result.Result
import java.util.regex.Pattern

private val cidPattern = Pattern.compile("^([a-zA-z0-9]|!|-)+:([a-zA-z0-9]|!|-)+$")

/**
 * Validates a cid. Verifies it's not empty and in the format messaging:123.
 *
 * @param cid The full channel id. ie messaging:123.
 *
 * @throws IllegalArgumentException If CID is invalid.
 */
@Throws(IllegalArgumentException::class)
public fun validateCid(cid: String): String = cid.apply {
    require(cid.isNotEmpty()) { "cid can not be empty" }
    require(cid.isNotBlank()) { "cid can not be blank" }
    // require(cidPattern.matcher(cid).matches()) {
    //     "cid needs to be in the format channelType:channelId. For example, messaging:123"
    // }
}

/**
 * Safely validates a cid and returns a result.
 *
 * @param cid The full channel id. ie messaging:123.
 *
 * @return Successful [Result] if the cid is valid.
 */
public fun validateCidWithResult(cid: String): Result<String> {
    return try {
        Result.Success(validateCid(cid))
    } catch (exception: IllegalArgumentException) {
        Result.Failure(Error.ThrowableError(message = "Cid is invalid: $cid", cause = exception))
    }
}
