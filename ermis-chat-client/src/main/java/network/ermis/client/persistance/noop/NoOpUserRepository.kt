package network.ermis.client.persistance.noop

import network.ermis.client.persistance.UserRepository
import network.ermis.core.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * No-Op UserRepository.
 */
internal object NoOpUserRepository : UserRepository {
    override suspend fun insertUsers(users: Collection<User>) { /* No-Op */ }
    override suspend fun insertUser(user: User) { /* No-Op */ }
    override suspend fun insertCurrentUser(user: User) { /* No-Op */ }
    override suspend fun selectUser(userId: String): User? = null
    override suspend fun selectUsers(ids: List<String>): List<User> = emptyList()
    override fun observeLatestUsers(): StateFlow<Map<String, User>> = MutableStateFlow(emptyMap())
    override suspend fun clear() { /* No-Op */ }
}
