
package network.ermis.sample.util.extensions

import network.ermis.core.models.ChannelData

public fun ChannelData.isAnonymousChannel(): Boolean = cid.startsWith("messaging")
