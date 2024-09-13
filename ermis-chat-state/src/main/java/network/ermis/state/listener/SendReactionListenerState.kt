package network.ermis.state.listener

import network.ermis.state.plugin.logic.LogicRegistry
import io.getstream.result.Error
import io.getstream.result.Result
import network.ermis.client.setup.ClientState
import network.ermis.client.utils.extensions.cidToTypeAndId
import network.ermis.client.utils.extensions.internal.addMyReaction
import network.ermis.client.utils.extensions.internal.enrichWithDataBeforeSending
import network.ermis.core.errors.isPermanent
import network.ermis.core.models.Message
import network.ermis.core.models.Reaction
import network.ermis.core.models.SyncStatus
import network.ermis.core.models.User

/**
 * State implementation for SendReactionListener. It updates the state accordingly and does the optimistic UI update.
 *
 * @param logic [LogicRegistry] Handles the state of channels.
 * @param clientState [ClientState] Check the state of the SDK.
 */
internal class SendReactionListenerState(
    private val logic: LogicRegistry,
    private val clientState: ClientState,
) : network.ermis.client.plugin.listeners.SendReactionListener {

    /**
     * A method called before making an API call to send the reaction.
     * runs optimistic update if the message and channel can be found in memory.
     *
     * @param cid The full channel id, i.e. "messaging:123".
     * @param reaction The [Reaction] to send.
     * @param enforceUnique Flag to determine whether the reaction should replace other ones added by the current user.
     * @param currentUser The currently logged in user.
     */
    override suspend fun onSendReactionRequest(
        cid: String?,
        reaction: Reaction,
        enforceUnique: Boolean,
        currentUser: User,
    ) {
        val reactionToSend = reaction.enrichWithDataBeforeSending(
            currentUser = currentUser,
            isOnline = clientState.isNetworkAvailable,
            enforceUnique = enforceUnique,
        )

        val channelLogic = cid?.cidToTypeAndId()?.let { (type, id) -> logic.channel(type, id) }
            ?: logic.channelFromMessageId(reaction.messageId)
        val cachedChannelMessage = channelLogic?.getMessage(reaction.messageId)
            ?.addMyReaction(reaction = reactionToSend, enforceUnique = enforceUnique)
        cachedChannelMessage?.let(channelLogic::upsertMessage)

        val threadLogic = logic.threadFromMessageId(reaction.messageId)
        val cachedThreadMessage = threadLogic?.getMessage(reaction.messageId)
            ?.addMyReaction(reaction = reactionToSend, enforceUnique = enforceUnique)
        cachedThreadMessage?.let(threadLogic::upsertMessage)
    }

    override suspend fun onSendReactionResult(
        cid: String?,
        reaction: Reaction,
        enforceUnique: Boolean,
        currentUser: User,
        result: Result<Reaction>,
    ) {
        val channelLogic = cid?.cidToTypeAndId()?.let { (type, id) -> logic.channel(type, id) }
            ?: logic.channelFromMessageId(reaction.messageId)
        channelLogic?.getMessage(reaction.messageId)?.let { message ->
            channelLogic.upsertMessage(
                message.updateReactionSyncStatus(
                    originReaction = reaction,
                    result = result,
                ),
            )
        }

        val threadLogic = logic.threadFromMessageId(reaction.messageId)
        threadLogic?.getMessage(reaction.messageId)?.let { message ->
            threadLogic.upsertMessage(
                message.updateReactionSyncStatus(
                    originReaction = reaction,
                    result = result,
                ),
            )
        }
    }

    /**
     * Checks if current user is set and reaction contains required data.
     *
     * @param currentUser The currently logged in user.
     * @param reaction The [Reaction] to send.
     */
    override suspend fun onSendReactionPrecondition(currentUser: User?, reaction: Reaction): Result<Unit> {
        return when {
            currentUser == null -> {
                Result.Failure(Error.GenericError(message = "Current user is null!"))
            }
            reaction.messageId.isBlank() || reaction.type.isBlank() -> {
                Result.Failure(
                    Error.GenericError(
                        message = "Reaction::messageId and Reaction::type cannot be empty!",
                    ),
                )
            }
            else -> {
                Result.Success(Unit)
            }
        }
    }

    private fun Message.updateReactionSyncStatus(originReaction: Reaction, result: Result<*>): Message = this.copy(
        ownReactions = ownReactions
            .map { ownReaction ->
                when (ownReaction.id) {
                    originReaction.id -> ownReaction.updateSyncStatus(result)
                    else -> ownReaction
                }
            },
        latestReactions = latestReactions
            .map { latestReaction ->
                when (latestReaction.id) {
                    originReaction.id -> latestReaction.updateSyncStatus(result)
                    else -> latestReaction
                }
            },
    )

    private fun Reaction.updateSyncStatus(result: Result<*>): Reaction = this.copy(
        syncStatus = when (result) {
            is Result.Success -> SyncStatus.COMPLETED
            is Result.Failure -> when {
                result.value.isPermanent() -> SyncStatus.FAILED_PERMANENTLY
                else -> SyncStatus.SYNC_NEEDED
            }
        },
    )
}