package network.ermis.offline.repository.domain.user

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * The UserEntity, id is a required field.
 *
 * You can convert a User object from the low level client to a UserEntity like this:
 * val userEntity = UserEntity(user)
 * and back:
 * userEntity.toUser()
 *
 * @param id The unique id of the user. This field if required.
 * @param originalId Used for storing the current user.
 * @param name User's name.
 * @param image User's image.
 * @param role Determines the set of user permissions.
 * @param createdAt Date/time of creation.
 * @param updatedAt Date/time of the last update.
 * @param lastActive Date of last activity.
 * @param invisible Determines if the user should share its online status. Can only be changed while connecting the user.
 * @param banned Whether a user is banned or not.
 * @param mutes A list of users muted by the current user.
 * @param extraData A map of custom fields for the user.
 */
@Entity(tableName = USER_ENTITY_TABLE_NAME)
internal data class UserEntity(
    @PrimaryKey val id: String,
    val originalId: String = "",
    @ColumnInfo(index = true)
    val name: String,
    val image: String,
    val role: String = "",
    val createdAt: Date? = null,
    val updatedAt: Date? = null,
    val lastActive: Date? = null,
    val invisible: Boolean = false,
    val banned: Boolean = false,
    val mutes: List<String> = emptyList(),
    val extraData: Map<String, Any> = emptyMap(),
)

internal const val USER_ENTITY_TABLE_NAME = "stream_chat_user"
