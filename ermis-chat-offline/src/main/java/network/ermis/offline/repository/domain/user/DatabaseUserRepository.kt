package network.ermis.offline.repository.domain.user

import androidx.collection.LruCache
import io.getstream.log.taggedLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import network.ermis.client.persistance.UserRepository
import network.ermis.core.models.User
import network.ermis.offline.extensions.launchWithMutex

internal class DatabaseUserRepository(
    private val scope: CoroutineScope,
    private val userDao: UserDao,
    cacheSize: Int = 1000,
) : UserRepository {
    private val logger by taggedLogger("Chat:UserRepository")

    // the user cache is simple, just keeps the last 100 users in memory
    private val userCache = LruCache<String, User>(cacheSize)
    private val latestUsersFlow: MutableStateFlow<Map<String, User>> = MutableStateFlow(emptyMap())
    private val dbMutex = Mutex()

    override fun observeLatestUsers(): StateFlow<Map<String, User>> = latestUsersFlow

    override suspend fun clear() {
        dbMutex.withLock {
            userDao.deleteAll()
        }
    }

    /**
     * Insert many users.
     *
     * @param users collection of [User]
     */
    override suspend fun insertUsers(users: Collection<User>) {
        if (users.isEmpty()) return
        val userIds = users.map { it.id }.toList()
        val userIdsInDB = userDao.select(userIds).map { it.id }.toList()
        val usersCanUpdate = users
            .filter { it != userCache[it.id] }
            .filter { userIdsInDB.contains(it.id).not() || (userIdsInDB.contains(it.id) && it.name.isEmpty().not()) }
        val usersToInsert = usersCanUpdate.map { it.toEntity() }
        cacheUsers(usersCanUpdate)
        scope.launchWithMutex(dbMutex) {
            logger.v { "[insertUsers] inserting ${usersToInsert.size} entities on DB, updated ${users.size} on cache" }
            usersToInsert
                .takeUnless { it.isEmpty() }
                ?.let { userDao.insertMany(it) }
        }
    }

    /**
     * Inserts a users.
     *
     * @param user [User]
     */
    override suspend fun insertUser(user: User) {
        insertUsers(listOf(user))
    }

    /**
     * Inserts the current user of the SDK.
     *
     * @param user [User]
     */
    override suspend fun insertCurrentUser(user: User) {
        insertUser(user)
        scope.launchWithMutex(dbMutex) {
            val userEntity = user.toEntity().copy(id = ME_ID)
            userDao.insert(userEntity)
        }
    }

    /**
     * Selects a user by id.
     *
     * @param userId String.
     */
    override suspend fun selectUser(userId: String): User? {
        return userCache[userId] ?: userDao.select(userId)?.let(::toModel)?.also { cacheUsers(listOf(it)) }
    }

    /**
     * @return The list of users stored in the cache.
     */
    override suspend fun selectUsers(ids: List<String>): List<User> {
        val cachedUsers = ids.mapNotNullTo(mutableListOf(), userCache::get)
        val missingUserIds = ids.minus(cachedUsers.map(User::id).toSet())

        return cachedUsers + userDao.select(missingUserIds).map(::toModel).also { cacheUsers(it) }
    }

    private fun cacheUsers(users: Collection<User>) {
        for (userEntity in users) {
            userCache.put(userEntity.id, userEntity)
        }
        scope.launch { latestUsersFlow.value = userCache.snapshot() }
    }

    private fun User.toEntity(): UserEntity =
        UserEntity(
            id = id,
            name = name,
            image = image,
            originalId = id,
            role = role,
            createdAt = createdAt,
            updatedAt = updatedAt,
            lastActive = lastActive,
            invisible = isInvisible,
            banned = isBanned,
            extraData = extraData,
            mutes = mutes.map { mute -> mute.target.id },
        )

    private fun toModel(userEntity: UserEntity): User = with(userEntity) {
        User(
            id = this.originalId,
            name = name,
            image = image,
            role = role,
            createdAt = createdAt,
            updatedAt = updatedAt,
            lastActive = lastActive,
            invisible = invisible,
            extraData = extraData.toMutableMap(),
            banned = banned,
        )
    }

    companion object {
        private const val ME_ID = "me"
    }
}
