package network.ermis.ui.common.utils.extensions

import network.ermis.client.ErmisClient
import network.ermis.core.models.Channel

public val Channel.initials: String
    get() = name.initials()

public fun Channel.isDirectMessaging(): Boolean {
    return type == "messaging" || cid.startsWith("messaging")
}

private fun Channel.includesCurrentUser(): Boolean {
    val currentUserId = ErmisClient.instance().clientState.user.value?.id ?: return false
    return members.any { it.user.id == currentUserId }
}
