package network.ermis.offline.plugin.listener

import network.ermis.client.utils.extensions.internal.toCid
import network.ermis.client.persistance.ChannelRepository
import network.ermis.client.persistance.UserRepository
import network.ermis.client.plugin.listeners.QueryMembersListener
import network.ermis.core.models.FilterObject
import network.ermis.core.models.Member
import network.ermis.core.models.User
import network.ermis.core.models.querysort.QuerySorter
import io.getstream.result.Error
import io.getstream.result.Result

/**
 * [QueryMembersListener] implementation for [io.getstream.chat.android.offline.plugin.internal.OfflinePlugin].
 * Handles updating members in the database.
 *
 * @param userRepository [UserRepository] to cache intermediate data and final result related to users.
 * @param channelRepository [ChannelRepository] to cache intermediate data and final result related to channels.
 */
internal class QueryMembersListenerDatabase(
    private val userRepository: UserRepository,
    private val channelRepository: ChannelRepository,
) : network.ermis.client.plugin.listeners.QueryMembersListener {

    override suspend fun onQueryMembersResult(
        result: Result<List<Member>>,
        channelType: String,
        channelId: String,
        offset: Int,
        limit: Int,
        filter: FilterObject,
        sort: QuerySorter<Member>,
        members: List<Member>,
    ) {
        if (result is Result.Success) {
            val resultMembers = result.value

            userRepository.insertUsers(resultMembers.map(Member::user))
            channelRepository.updateMembersForChannel(Pair(channelType, channelId).toCid(), resultMembers)
        }
    }

    override suspend fun onQueryUserListResult(result: Result<List<User>>) {
        if (result is Result.Success) {
            val resultUsers = result.value
            userRepository.insertUsers(resultUsers)
        }
    }

    override suspend fun onGetInfoCurrentUserResult(result: Result<User>) {
        if (result is Result.Success) {
            val user = result.value
            userRepository.insertUsers(listOf(user))
        }
    }

    override suspend fun getUserDB(userId: String): Result<User> {
        val user = userRepository.selectUser(userId)
        return if (user != null) {
            Result.Success(user)
        } else {
            Result.Failure(Error.GenericError("This user is not available in BD offline"))
        }
    }
}
