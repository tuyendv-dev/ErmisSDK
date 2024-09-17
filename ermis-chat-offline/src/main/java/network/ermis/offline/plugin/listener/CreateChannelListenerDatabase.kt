package network.ermis.offline.plugin.listener

import io.getstream.result.Error
import io.getstream.result.Result
import network.ermis.client.persistance.ChannelRepository
import network.ermis.client.persistance.UserRepository
import network.ermis.client.plugin.listeners.CreateChannelListener
import network.ermis.client.setup.ClientState
import network.ermis.client.utils.channel.generateChannelIdIfNeeded
import network.ermis.core.errors.isPermanent
import network.ermis.core.models.Channel
import network.ermis.core.models.Member
import network.ermis.core.models.SyncStatus
import network.ermis.core.models.User
import java.util.Date

/**
 * [CreateChannelListener] implementation for [io.getstream.chat.android.offline.plugin.internal.OfflinePlugin].
 * Handles creating the channel offline and updates the database.
 * Does not perform optimistic UI update as it's impossible to determine whether a particular channel should be visible
 * for the current user or not.
 *
 * @param clientState [ClientState]
 * @param channelRepository [ChannelRepository] to cache intermediate data and final result of channels.
 * @param userRepository [UserRepository] Requests users from database.
 */
internal class CreateChannelListenerDatabase(
    private val clientState: ClientState,
    private val channelRepository: ChannelRepository,
    private val userRepository: UserRepository,
) : network.ermis.client.plugin.listeners.CreateChannelListener {

    /**
     * A method called before making an API call to create the channel.
     * Creates the channel based on provided data and updates the database.
     * Channel's id will be automatically generated based on the members list if provided id is empty.
     *
     * @param channelType The channel type. ie messaging.
     * @param channelId The channel id. ie 123.
     * @param memberIds The list of members' ids.
     * @param extraData Map of key-value pairs that let you store extra data
     * @param currentUser The currently logged in user.
     */
    override suspend fun onCreateChannelRequest(
        channelType: String,
        channelId: String,
        memberIds: List<String>,
        extraData: Map<String, Any>,
        currentUser: User,
    ) {
        val generatedChannelId = generateChannelIdIfNeeded(channelId, memberIds)
        val channel = Channel(
            id = generatedChannelId,
            type = channelType,
            members = getMembers(memberIds),
            extraData = extraData.toMutableMap(),
            createdAt = Date(),
            createdBy = currentUser,
            syncStatus = if (clientState.isOnline) SyncStatus.IN_PROGRESS else SyncStatus.SYNC_NEEDED,
            name = extraData["name"] as? String ?: "",
            image = extraData["image"] as? String ?: "",
        )

        channelRepository.insertChannel(channel)
    }

    /**
     * Converts member's id to [Member] object.
     * Tries to fetch users from cache and fallbacks to the empty [User] object with an [User.id] if the user
     * wasn't cached yet.
     *
     * @return The list of members.
     */
    private suspend fun getMembers(memberIds: List<String>): List<Member> {
        val cachedUsers = userRepository.selectUsers(memberIds)
        val missingUserIds = memberIds.minus(cachedUsers.map(User::id).toSet())

        return (cachedUsers + missingUserIds.map(::User)).map(::Member)
    }

    /**
     * A method called after receiving the response from the create channel call.
     * Updates channel's sync status stored in the database based on API result.
     *
     * @param channelType The channel type. ie messaging.
     * @param channelId The channel id. ie 123.
     * @param memberIds The list of members' ids.
     * @param result The API call result.
     */
    override suspend fun onCreateChannelResult(
        channelType: String,
        channelId: String,
        memberIds: List<String>,
        result: Result<Channel>,
    ) {
        val generatedCid = "$channelType:${generateChannelIdIfNeeded(channelId, memberIds)}"
        when (result) {
            is Result.Success -> {
                val channel = result.value.copy(syncStatus = SyncStatus.COMPLETED)

                // Generated if might differ from the actual one. This might happen when the channel already exists.
                if (channel.cid != generatedCid) {
                    channelRepository.deleteChannel(generatedCid)
                }
                channelRepository.insertChannel(channel)
            }
            is Result.Failure -> {
                channelRepository.selectChannels(listOf(generatedCid)).firstOrNull()?.let { cachedChannel ->
                    channelRepository.insertChannel(
                        cachedChannel.copy(
                            syncStatus = if (result.value.isPermanent()) {
                                SyncStatus.FAILED_PERMANENTLY
                            } else {
                                SyncStatus.SYNC_NEEDED
                            },
                        ),
                    )
                }
            }
        }
    }

    /**
     * Checks if current user is set and channel's id conditions are met.
     *
     * @param currentUser The currently logged in user.
     * @param channelId The channel id. ie 123.
     * @param memberIds The list of members' ids.
     */
    override fun onCreateChannelPrecondition(
        currentUser: User?,
        channelId: String,
        memberIds: List<String>,
    ): Result<Unit> {
        return when {
            channelId.isBlank() && memberIds.isEmpty() -> {
                Result.Failure(Error.GenericError(message = "Either channelId or memberIds cannot be empty!"))
            }
            currentUser == null -> {
                Result.Failure(Error.GenericError(message = "Current user is null!"))
            }
            else -> {
                Result.Success(Unit)
            }
        }
    }
}
