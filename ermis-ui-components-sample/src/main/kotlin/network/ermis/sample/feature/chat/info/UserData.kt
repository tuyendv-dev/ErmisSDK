
package network.ermis.sample.feature.chat.info

import network.ermis.core.models.User
import java.io.Serializable
import java.util.Date

data class UserData(
    val id: String,
    val name: String,
    val image: String,
    val online: Boolean,
    val createdAt: Date?,
    val lastActive: Date?,
) : Serializable

fun UserData.toUser(): User = User(
    id = id,
    name = name,
    image = image,
    online = online,
    createdAt = createdAt,
    lastActive = lastActive,
)

fun User.toUserData() = UserData(
    id = id,
    name = name,
    image = image,
    online = online,
    createdAt = createdAt,
    lastActive = lastActive,
)
