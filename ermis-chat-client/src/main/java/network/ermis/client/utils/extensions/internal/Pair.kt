package network.ermis.client.utils.extensions.internal

import network.ermis.client.utils.internal.validateCid

/**
 * Converts a pair of channelType and channelId into cid.
 *
 * @return String CID of the given channel type and id.
 * @throws IllegalArgumentException if cid is not valid.
 */
@Throws(IllegalArgumentException::class)
public fun Pair<String, String>.toCid(): String {
    val cid = "$first:$second"
    validateCid(cid)
    return cid
}
