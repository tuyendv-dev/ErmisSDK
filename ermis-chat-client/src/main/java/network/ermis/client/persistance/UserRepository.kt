package network.ermis.client.persistance

import network.ermis.core.models.User
import kotlinx.coroutines.flow.StateFlow

/**
 * Repository to read and write data about users.
 */
public interface UserRepository {

    /**
     * Insert many users.
     *
     * @param users collection of [User]
     */
    public suspend fun insertUsers(users: Collection<User>)

    /**
     * Inserts a users.
     *
     * @param user [User]
     */
    public suspend fun insertUser(user: User)

    /**
     * Inserts the current user of the SDK.
     *
     * @param user [User]
     */
    public suspend fun insertCurrentUser(user: User)

    /**
     * Selects a user by id.
     *
     * @param userId String.
     */
    public suspend fun selectUser(userId: String): User?

    /**
     * @return The list of users stored in the cache.
     */
    public suspend fun selectUsers(ids: List<String>): List<User>

    /**
     * Returns flow of latest updated users.
     */
    public fun observeLatestUsers(): StateFlow<Map<String, User>>

    /**
     * Clear users of this repository.
     */
    public suspend fun clear()
}
