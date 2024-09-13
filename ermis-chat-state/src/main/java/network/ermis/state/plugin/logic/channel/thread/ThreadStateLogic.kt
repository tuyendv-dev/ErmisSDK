package network.ermis.state.plugin.logic.channel.thread

import network.ermis.client.utils.extensions.internal.NEVER
import network.ermis.core.models.Message
import network.ermis.core.models.SyncStatus
import network.ermis.state.plugin.state.channel.thread.ThreadMutableState

/**
 * The logic of the state of a thread. This class contains the logic of how to
 * update the state of the thread in the SDK.
 *
 * @property mutableState [ThreadMutableState]
 */
internal class ThreadStateLogic(private val mutableState: ThreadMutableState) {

    /**
     * Return [ThreadMutableState] representing the state of the thread. Use this when you would like to
     * keep track of the state and would like to write a new state too.
     */
    fun writeThreadState(): ThreadMutableState = mutableState

    /**
     * Deletes a message for the thread
     *
     * @param message [Message]
     */
    fun deleteMessage(message: Message) {
        mutableState.deleteMessage(message)
    }

    /**
     * Upsert message in the thread.
     *
     * @param message The message to be added or updated.
     */
    fun upsertMessage(message: Message) = upsertMessages(listOf(message))

    /**
     * Upsert messages in the channel.
     *
     * @param messages the list of [Message] to be upserted
     * new messages should be kept.
     */
    fun upsertMessages(messages: List<Message>) {
        val oldMessages = mutableState.rawMessage.value
        mutableState.upsertMessages(
            messages.filter { newMessage -> isMessageNewerThanCurrent(oldMessages[newMessage.id], newMessage) },
        )
    }

    private fun isMessageNewerThanCurrent(currentMessage: Message?, newMessage: Message): Boolean {
        return if (newMessage.syncStatus == SyncStatus.COMPLETED) {
            (currentMessage?.lastUpdateTime() ?: NEVER.time) <= newMessage.lastUpdateTime()
        } else {
            (currentMessage?.lastLocalUpdateTime() ?: NEVER.time) <= newMessage.lastLocalUpdateTime()
        }
    }

    private fun Message.lastUpdateTime(): Long = listOfNotNull(
        createdAt,
        updatedAt,
        deletedAt,
    ).maxOfOrNull { it.time }
        ?: NEVER.time

    private fun Message.lastLocalUpdateTime(): Long = listOfNotNull(
        createdLocallyAt,
        updatedLocallyAt,
        deletedAt,
    ).maxOfOrNull { it.time }
        ?: NEVER.time
}
